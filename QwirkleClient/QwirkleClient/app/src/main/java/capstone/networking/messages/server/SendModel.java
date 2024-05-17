package capstone.networking.messages.server;

import capstone.QwirkleModel;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;

public class SendModel extends Message {
    private static final long serialVersionUID = 423L;
    public QwirkleModel model;

    public SendModel(QwirkleModel model) {
        this.model = model;
    }
}
