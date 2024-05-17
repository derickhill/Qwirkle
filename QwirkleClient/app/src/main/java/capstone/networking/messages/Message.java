package capstone.networking.messages;

import java.io.Serializable;

/**
 * Note that this definition of the Message class differs
 * from the one in the Server (not generic, does not have
 * the apply method). This version is simply being used
 * for receiving data from the server.
 *
 * The serialVersionUIDs still match between server and
 * client, the full class names and fields still match.
 * This makes it possible for the Object streams to
 * transfer data seamlessly between the two.
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 999L;
}
