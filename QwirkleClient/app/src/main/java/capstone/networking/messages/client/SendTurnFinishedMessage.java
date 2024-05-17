package capstone.networking.messages.client;


import capstone.QwirkleModel;
import capstone.networking.messages.Message;

public class SendTurnFinishedMessage extends Message {
    private static final long serialVersionUID = 5L;

    public QwirkleModel model;

    public SendTurnFinishedMessage(QwirkleModel model) {
        this.model = model;
    }


}
