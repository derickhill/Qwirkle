package capstone.networking.messages.client;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;

import capstone.Player;
import capstone.QwirkleModel;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.SendModel;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WaitingForGame extends Message {
    private static final long serialVersionUID = 123L;
    Player player;

    public WaitingForGame(Player player)
    {
        this.player = player;
    }
}
