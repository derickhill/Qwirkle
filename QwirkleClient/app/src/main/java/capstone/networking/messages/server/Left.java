package capstone.networking.messages.server;

import capstone.networking.messages.Message;

public class Left extends Message {
    private static final long serialVersionUID = 103L;

    public String groupName;
    public String handle;

    @Override
    public String toString() {
        return String.format("Left('%s', '%s')", groupName, handle);
    }
}
