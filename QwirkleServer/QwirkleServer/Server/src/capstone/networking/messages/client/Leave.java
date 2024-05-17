package capstone.networking.messages.client;


import capstone.networking.Games;
import capstone.networking.messages.Message;
import capstone.networking.QwirkleClient;

/**
 * The message received when a client wishes to leave the
 * current chat game that they are in.
 */
public class Leave extends Message<QwirkleClient> {
    private static final long serialVersionUID = 2L;

    @Override
    public String toString() {
        return "Leave()";
    }

    @Override
    public void apply(QwirkleClient qwirkleClient) {
        // Client leaves the game they are currently in.
        Games.leave(qwirkleClient);
    }
}
