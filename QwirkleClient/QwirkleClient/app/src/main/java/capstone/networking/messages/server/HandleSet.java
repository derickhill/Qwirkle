package capstone.networking.messages.server;


import capstone.networking.messages.Message;

public class HandleSet extends Message {
    private static final long serialVersionUID = 104L;

    public String handle;

    public HandleSet(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return String.format("HandleSet('%s')", handle);
    }
}
