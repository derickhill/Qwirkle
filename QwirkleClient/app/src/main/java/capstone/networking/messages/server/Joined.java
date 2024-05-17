package capstone.networking.messages.server;

import capstone.networking.messages.Message;

public class Joined extends Message {
    private static final long serialVersionUID = 102L;

    public String groupName;
    public String handle;

    @Override
    public String toString() {
        return String.format("Joined(%s, %s)", groupName, handle);
    }
}
