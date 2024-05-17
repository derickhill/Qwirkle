package capstone.networking;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class QwirkleServer {
    public static void main(String[] args) throws Exception {
        String answer = JOptionPane.showInputDialog("Would you like to run the server using a link local address (for emulators)? (Y/N)");
        boolean linkLocal = false;

        String bindaddr = "192.168.1.36";

        if(answer.toUpperCase().charAt(0) == 'Y') {
                linkLocal = true;
                bindaddr = InetAddress.getLocalHost().getHostAddress();
            }

        if(!linkLocal)
        {
            bindaddr = JOptionPane.showInputDialog("What is your IPv4 address?");
        }

        new QwirkleServer(bindaddr, linkLocal);
    }

    // The server socket that listens to port 4444 for connection requests.
    private ServerSocket server;

    private int clientNum = 0;

    public QwirkleServer(String bindaddr, boolean local) throws Exception {
        // Start new server socket on port 5050.

        server = new ServerSocket(4444, 100, InetAddress.getByName(bindaddr));
        System.out.printf("Qwirkle server started on: %s:4444\n",
                InetAddress.getByName(bindaddr));

        // Create the initial game to which all clients belong.
        Games.addGame("lobby");

        while(true) {
            // Accept connection requests.
            Socket client = server.accept();
            System.out.printf("Connection request received: %s\n", client.getInetAddress().getHostAddress());

            // Increment number of clients encountered.
            clientNum++;

            // Create new client connection object to manage.
            QwirkleClient qwirkleClient = new QwirkleClient(client, clientNum);
        }
    }
}
