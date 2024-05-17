package capstone.networking.messages.client;

import capstone.QwirkleModel;
import capstone.networking.messages.Message;

public class GameOver extends Message {
    private static final long serialVersionUID = 7776L;
    public QwirkleModel model;

   public GameOver(QwirkleModel model)
   {
       this.model = model;
   }
}
