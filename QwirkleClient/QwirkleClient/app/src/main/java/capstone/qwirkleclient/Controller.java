package capstone.qwirkleclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import capstone.Player;
import capstone.QwirkleModel;
import capstone.Tile;

public class Controller {
    public static boolean containsPosition(List<Tile.Position> possiblePlaces, int i, int j)
    {
        if(possiblePlaces != null)
        {
            for (Tile.Position pos : possiblePlaces)
            {
                if(i == pos.x && j == pos.y)
                    return true;
            }
        }
        return false;
    }

    public static void goToResults(Context packageContext, QwirkleModel model)
    {
        Intent intent = new Intent(packageContext, ResultsActivity.class);
        intent.putExtra("model", model);
        packageContext.startActivity(intent);
    }

    public static void swapping(boolean swapping, Button btnSwap)
    {
        if(!swapping)
        {
            btnSwap.setBackgroundColor(Color.BLUE);
            btnSwap.setText(btnSwap.getContext().getString(R.string.Swap));
            return;
        }

        btnSwap.setBackgroundColor(Color.GRAY);
        btnSwap.setText(btnSwap.getContext().getString(R.string.Swapping));
    }

    public static void qwirkle(Context context, View v)
    {
        MediaPlayer.create(context, R.raw.qwirkle).start();
        Toast.makeText(v.getContext(), context.getString(R.string.app_name) + "!", Toast.LENGTH_SHORT).show();
    }

    public static void updateScore(TextView txtScore, Player player)
    {
        txtScore.setText(new StringBuilder().append(txtScore.getContext().getString(R.string.Score)).append(": ").append(player.score).toString());
    }
}
