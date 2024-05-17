package capstone.networking.messages;

import java.io.Serializable;

/**
 * A message that will be sent OR received on Object streams to and
 * from clients. Because want to use Object streams, needs to implement
 * the Serializable interface.
 *
 * The apply method is provided as a way for capstone.messages from a client to
 * be processed (applied to some context). The type of the context is
 * a generic and in these examples will be a ChatClient.
 *
 * Note: the exact definition of the classes in the Java server and the
 * Android client will differ slightly (in terms of methods, not fields).
 * In order for the slightly different classes to match when reading/writing
 * each class will have a <code>private static final long serialVersionUID</code>
 * that will be used to perform the matching.
 * @param <C> The type of the context.
 */
public abstract class Message<C> implements Serializable {
    private static final long serialVersionUID = 999L;

    /**
     * Apply this message's logic to a specific context. This will only
     * be used for capstone.messages received from a client.
     * @param context The context to apply the message logic too.
     */
    public void apply(C context) {}

    //public abstract void apply();
}
