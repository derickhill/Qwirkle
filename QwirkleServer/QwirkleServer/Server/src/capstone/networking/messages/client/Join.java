package capstone.networking.messages.client;


import capstone.networking.Games;
import capstone.networking.messages.Message;
import capstone.networking.QwirkleClient;

/**
 * The message received when a client wishes to join a different
 * chat game.
 */
public class Join extends Message<QwirkleClient> {
    private static final long serialVersionUID = 1L;
    public String gameName;

    public Join(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return String.format("Join('%s')", gameName);
    }

    @Override
    public void apply(QwirkleClient chatClient) {
        Games.join(gameName, chatClient);
    }
}
