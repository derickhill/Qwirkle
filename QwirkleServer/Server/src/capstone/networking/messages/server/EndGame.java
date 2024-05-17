package capstone.networking.messages.server;

import capstone.QwirkleModel;
import capstone.networking.messages.Message;


public class EndGame extends Message {
    private static final long serialVersionUID = 1984L;

    public QwirkleModel model;

    public EndGame(QwirkleModel model)
    {
        this.model = model;
    }
}
