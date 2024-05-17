package capstone.networking.messages.server;


import capstone.networking.messages.Message;

/**
 * Message sent to all clients in a group when a client leaves the
 * group.
 */
public class Left extends Message {
    private static final long serialVersionUID = 103L;

    public String groupName;
    public String handle;

    public Left(String groupName, String handle) {
        this.groupName = groupName;
        this.handle = handle;
    }

    @Override
    public String toString() {
        return String.format("Left('%s', '%s')", groupName, handle);
    }
}
