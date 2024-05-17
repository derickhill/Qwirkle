package capstone.networking.messages.client;

import capstone.Player;
import capstone.QwirkleModel;
import capstone.networking.Games;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.SendModel;

public class WaitingForGame extends Message<QwirkleClient> {

    private static final long serialVersionUID = 123L;
    Player player;

    public WaitingForGame(Player player)
    {
        this.player = player;
    }

    @Override
    public void apply(QwirkleClient qwirkleClient)
    {
       QwirkleModel model = new QwirkleModel();

        int game = 1;
        boolean joined = false;
        String gameName = "";

        while(!joined) {
            gameName =  "Game " + game;

            int size = Games.getGameSize(gameName);

            model = Games.models.get(gameName);

            if (size < 4 && model.counter == 0) {
                Games.join(gameName, qwirkleClient);
                joined = true;
            }
            else {
                game++;
            }
        }

        model.addPlayer(player);
        Games.models.put(gameName, model);

        if(model.players.size() >= 2)
            Games.send(gameName, new SendModel(model));
    }
}
