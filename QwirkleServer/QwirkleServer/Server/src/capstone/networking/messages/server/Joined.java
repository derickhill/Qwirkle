package capstone.networking.messages.server;

import capstone.networking.messages.Message;

/**
 * Message sent to all clients in a group when a new client joins
 * a group.
 */
public class Joined extends Message {
    private static final long serialVersionUID = 102L;

    public String groupName;
    public String handle;

    public Joined(String groupName, String handle) {
        this.groupName = groupName;
        this.handle = handle;
    }

    @Override
    public String toString() {
        return String.format("Joined(%s, %s)", groupName, handle);
    }
}
