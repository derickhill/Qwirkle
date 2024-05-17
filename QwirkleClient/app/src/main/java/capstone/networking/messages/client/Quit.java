package capstone.networking.messages.client;


import capstone.networking.messages.Message;

public class Quit extends Message {
    private static final long serialVersionUID = 4L;

    @Override
    public String toString() {
        return "Quit()";
    }
}
