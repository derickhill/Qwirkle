package capstone.networking;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import capstone.QwirkleModel;
import capstone.networking.messages.Message;
import capstone.networking.messages.MessageReceiver;
import capstone.networking.messages.client.Leave;
import capstone.networking.messages.client.Quit;
import capstone.networking.messages.client.SendTurnFinishedMessage;
import capstone.networking.messages.client.SetHandle;

/**
 * This class is responsible for communicating with the server. It creates
 * two threads, one for Reading messages from the server and the other for
 * Writing messages to the server.
 *
 * When new messages are received, the client triggers an messageReceived
 * notification - which in this case is provided by the Main Activity.
 *
 * Messages to be sent to the server are placed in a queue, which the Writing
 * thread dequeues from and sends. This way, the UI thread does not directly
 * call the stream methods (which it's not allowed too) and if multiple messges
 * are sent in rapid succession, they cannot result in race conditions and
 * garbled messages.
 */
public class QwirkleClient implements Serializable {
    private String serverAddress;

    // I/O streams for communication.
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    // Using a thread-safe queue to handle multiple threads adding to the same
    // queue (potentially) and a single thread de-queueing and sending messages
    // across the network/internet.
    private BlockingQueue<Message> outgoingMessages;
    private MessageReceiver messageReceiver;

    // Thread that reads messages from the server.
    private Thread readThread;

    // Thread that writes messages to the server.
    private Thread writeThread;

    public String handle;

    /**
     * Enqueues a message to be sent to the server.
     * @param message The message being sent.
     */
    public void send(Message message) {
        try {
            outgoingMessages.put(message);
        } catch (InterruptedException e) {
            Log.e("QwirkleClient", e.getMessage());
        }
    }

    public QwirkleClient(MessageReceiver messageReceiver) {
        super();
        outgoingMessages = new LinkedBlockingQueue<>();
        this.messageReceiver = messageReceiver;
    }

    /**
     * Establish a connection to the server.
     * @param serverAddress The server's address.
     * @param handle The handle to be used for the client.
     */
    public void connect(String serverAddress, String handle) {
        this.handle = handle;

        Log.i("QwirkleClient", "Connecting to " + serverAddress + "...");

        // Cache info.
        this.serverAddress = serverAddress;
        // Details about the client.

        // Start the read thread (which establishes a connection.
        Log.i("QwirkleClient", "Starting Read Loop thread...");
        readThread = new ReadThread();
        readThread.start();

        // Send the setHandle message.
        Log.i("QwirkleClient", "Queuing SetHandle(" + handle + ")");
        send(new SetHandle(handle));
    }

    /**
     * Disconnect from the server.
     */
    public void disconnect() {
        leaveGroup();
        send(new Quit());
    }
    /**
     * Leave the current chat group.
     */
    public void leaveGroup() {
        send(new Leave());
    }

    /**
     * Send a text message to all clients in the current chat group.
     * @param model The text message to be sent.
     */
    public void sendTurnFinishedMessage(QwirkleModel model) {
        send(new SendTurnFinishedMessage(model));
    }

    /**
     * This thread is responsible for establishing a connection with the server,
     * starting the write thread and then going into a read loop which reads
     * messages from the server. When new messages arrive, a messageReceived
     * notification is generated.
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            Log.i("QwirkleClient", "Started Read Loop thread...");

            readThread = this;

            try {
                // Connect to server (if can).
                Socket connection = new Socket(serverAddress, 4444);
                Log.i("QwirkleClient", "Connected to " + serverAddress + "...");

                // Obtain I/O streams.
                // Possible GOTCHA: the order of obtaining the I/O streams in the server and
                // client is flipped. Needed due to synchronising the OBJECT streams on both ends.
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                Log.i("QwirkleClient", "Obtained I/O stream...");

                // Create and start the write thread.
                Log.i("QwirkleClient", "Starting Write Loop thread...");
                writeThread = new WriteThread();
                writeThread.start();

                // Go into read message loop.
                Log.i("QwirkleClient", "Starting Read Loop...");
                Message msg;
                do {
                    // Read message from server.
                    msg = (Message) in.readObject();
                    Log.i("QwirkleClient", ">> " + msg);

                    // If Message Receiver given, pass it the message.
                    if (messageReceiver != null) messageReceiver.messageReceived(msg);
                } while (msg.getClass() != Quit.class);

                // Done, so close connection.
                connection.close();
                Log.i("QwirkleClient", "Closed connection...");


            } catch (Exception e) {
                Log.e("QwirkleClient", "Exception: " + e.getMessage());

            } finally {
                readThread = null;
                if (writeThread != null) writeThread.interrupt();
            }
        }
    }

    /**
     * This thread is responsible for sending messages from the message queue
     * to the server. It is interrupted by the Read thread when it shuts down.
     */
    private class WriteThread extends Thread {
        @Override
        public void run() {
            Log.i("QwirkleClient", "Started Write Loop thread...");

            try {
                // Check outgoing messages and send.
                while (true) {
                    // Dequeue message to send. Take blocks until something to send.
                    Message msg = outgoingMessages.take();

                    out.writeObject(msg);
                    out.flush();

                    Log.i("QwirkleClient", "<< " + msg);
                }
            } catch (Exception e) {
                writeThread = null;
            }
        }
    }
}
