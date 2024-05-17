package capstone.networking.messages.client;

import capstone.networking.Games;
import capstone.networking.messages.Message;
import capstone.networking.QwirkleClient;

/**
 * The message received from a client when they no longer wish
 * to communicate with the server, i.e. log out.
 */
public class Quit extends Message<QwirkleClient> {
    private static final long serialVersionUID = 4L;

    @Override
    public String toString() {
        return "Quit()";
    }

    @Override
    public void apply(QwirkleClient qwirkleClient) {
        String gameName = qwirkleClient.gameName;
        Games.leave(qwirkleClient);
        Games.deleteGame(gameName);
    }
}
