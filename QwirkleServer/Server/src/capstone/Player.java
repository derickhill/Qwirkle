package capstone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        tiles = new ArrayList<Tile>();
        score = 0;
    }

    public Player()
    {
        this.handle = "Player " + num++;
        tiles = new ArrayList<Tile>();
        score = 0;
    }
}
