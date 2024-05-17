package capstone.networking.messages;

/**
 * A functional interface that is provided by an object
 * interested in being notified about when a message has
 * been received from the server.
 */
@FunctionalInterface
public interface MessageReceiver {
    void messageReceived(Message message);
}
