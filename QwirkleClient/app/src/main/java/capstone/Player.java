package capstone;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import capstone.qwirkleclient.R;

public class Player implements Serializable
{

    private static final long serialVersionUID = 1778L;
    public List<Tile> tiles;
    public int score;
    public String handle;
    public static int num = 1;

    public Player(String handle)
    {
        this.handle = handle;
        tiles = new ArrayList<>();
        score = 0;
    }

    public Player(Context context)
    {
        this.handle = context.getString(R.string.Player) + " " + num++;
        tiles = new ArrayList<>();
        score = 0;
    }
}
