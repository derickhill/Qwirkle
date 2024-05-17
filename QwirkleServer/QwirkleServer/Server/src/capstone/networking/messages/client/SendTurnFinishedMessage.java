package capstone.networking.messages.client;

import capstone.QwirkleModel;
import capstone.networking.Games;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.SendModel;

public class SendTurnFinishedMessage extends Message<QwirkleClient> {
    private static final long serialVersionUID = 5L;

    // The text message to be sent to all clients in the same game.
    public QwirkleModel model;

    public SendTurnFinishedMessage(QwirkleModel model) {
        this.model = model;
    }


    @Override
    public void apply(QwirkleClient qwirkleClient) {
        String gameName = qwirkleClient.gameName;
        // If not in a game, don't proceed.
        if (gameName.length() == 0) return;

        Games.models.put(gameName, model);
        // Send message to all clients in same game as client.
        Games.send(gameName,
                new SendModel(model));
    }
}
