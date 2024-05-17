package capstone.networking;

import capstone.networking.messages.Message;
import capstone.networking.messages.client.Quit;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class QwirkleClient implements Serializable {
    // Instance level fields.
    private Socket client;

    // I/O streams for communicating with client.
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Using a thread-safe queue to handle multiple threads adding to the same
    // queue (potentially) and a single thread de-queueing and sending capstone.messages
    // across the network/internet.
    private BlockingQueue<Message> outgoingMessages = new LinkedBlockingDeque<>();

    // Reads capstone.messages from this specific client.
    private ReadThread readThread;

    // Writes capstone.messages to this specific client.
    private WriteThread writeThread;

    // Details about the current connection's client.
    public int clientNum;
    public String handle = "";
    public String gameName = "";

    public QwirkleClient(Socket client, int clientNum) {
        this.client = client;
        this.clientNum = clientNum;

        // Start read loop thread.
        readThread = new ReadThread();
        readThread.start();

        // Add client to lobby.
        Games.join("lobby", QwirkleClient.this);

        // Queue game list to be sent to client.
        //send(new GamesListed());
    }

    /**
     *  Sending a message involves adding it to a queue of capstone.messages to be
     *  sent. The WriteThread will send ACTUALLY send the message on the connection
     *  to the client when it gets a turn to run.
     *
     *  Queueing capstone.messages prevents wierd things happening if multiple clients happen
     *  to be wanting to send to the SAME client at the SAME time and communication is
     *  not completed before the other starts sending. This can lead to garbled data
     *  being sent, due to race conditions.
     *
     *  Additionally, if there WERE a GUI, none of the writing on the output stream
     *  would happen on the UI thread. All that would be done on the UI thread is adding
     *  a message to a queue object.
     * @param message The message to be sent to this chat client.
     */
    public void send(Message message) {
        try {
            outgoingMessages.put(message);
        } catch (InterruptedException e) {
            System.out.println(clientNum + ": Read.Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This thread is responsible for setting up the I/O streams, then goes into
     * a read loop in which capstone.messages are read from the client (a blocking operation),
     * then processed. When shutting down the read thread, the write thread is
     * interrupted to stop it as well.
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println(clientNum + ": Read thread started.");

                // Obtain I/O streams. Gotcha for object streams is to make sure
                // that both sides do not set up the input stream first. One must
                // set up the output stream and flush it, else the other side will
                // wait for its input stream to be initialised (which it never will).
                // Remember this side's output is other side's input.
                out = new ObjectOutputStream(client.getOutputStream());
                out.flush();
                in = new ObjectInputStream(client.getInputStream());
                System.out.println(clientNum + ": Obtained I/O streams.");

                // Start write loop thread. Start write thread here to ensure
                // that the I/O streams have been initialised correctly BEFORE
                // starting to read and write capstone.messages.
                writeThread = new WriteThread();
                writeThread.start();

                // Read capstone.messages from client.
                System.out.println(clientNum + ": Started Read Loop...");
                Message msg;
                do {
                    // Read next message (blocking operation).
                    msg = (Message) in.readObject();
                    System.out.println(clientNum + " --> " + msg);

                    // Process the message.
                    msg.apply(QwirkleClient.this);

                } while (msg.getClass() != Quit.class);

                // Close the connection.
                client.close();

            } catch (Exception e) {
                System.out.println(clientNum + ": Read.Exception: " + e.getMessage());
                e.printStackTrace();

            } finally {
                System.out.println(clientNum + ": Leaving games...");
                Games.leave(QwirkleClient.this);

                System.out.println(clientNum + ": Stopping Write thread...");
                writeThread.interrupt();

                System.out.println(clientNum + ": Read thread finished.");
            }
        }
    }

    /**
     * This thread is responsible for dequeueing capstone.messages (blocking operation) and
     * then sending them to the client.
     */
    private class WriteThread extends Thread {
        @Override
        public void run() {
            System.out.println(clientNum + ": Started Write Loop thread...");

            // Remember this thread.
            writeThread = this;

            try {
                // Check outgoing capstone.messages and send.
                while (!isInterrupted()) {
                    // Dequeue message to send. Take blocks until something to send.
                    Message msg = outgoingMessages.take();

                    out.writeObject(msg);
                    out.flush();

                    System.out.println(msg + " --> " + clientNum);
                }

            } catch (Exception e) {
                System.out.println(clientNum + ": Write.Exception = " + e.getMessage());
                e.printStackTrace();

            } finally {
                writeThread = null;
                System.out.println(clientNum + ": Write thread finished.");
            }
        }
    }
}
