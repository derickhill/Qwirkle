package capstone.networking.messages.client;

import capstone.QwirkleModel;
import capstone.networking.Games;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.EndGame;

public class GameOver extends Message<QwirkleClient> {
    private static final long serialVersionUID = 7776L;
    public QwirkleModel model;

    @Override
    public void apply(QwirkleClient qwirkleClient)
    {
        Games.send(qwirkleClient.gameName, new EndGame(model));
    }
}
