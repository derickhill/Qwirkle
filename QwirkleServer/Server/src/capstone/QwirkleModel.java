package capstone;

import java.io.Serializable;
import java.util.*;

public class QwirkleModel implements Serializable
{
    private static final long serialVersionUID = 1423L;
    public List<Player> players;
    public List<Tile> bag;
    public Player curPlayer;
    public int counter;
    public List<Tile> allTiles;
    public boolean madeOneMove;
    public List<Tile> latestMoveSet;

    private void fillBag()
    {
        bag = new ArrayList<Tile>();

        for(int i = 0; i < Tile.Shape.values().length; i++)
        {
            for(int j = 0; j < Tile.Colour.values().length; j++)
            {
                for(int k = 0; k < 3; k++)
                {
                    Tile toAdd = new Tile();
                    toAdd.shape = Tile.Shape.values()[i];
                    toAdd.colour = Tile.Colour.values()[j];

                    bag.add(toAdd);
                }
            }
        }
    }

    public void assignTiles(Player player)
    {
        while(player.tiles.size() < 6 && bag.size() > 0)
        {
            player.tiles.add(getRandomTile());
        }
    }

    private Tile getRandomTile()
    {
        Random rand = new Random();
        Tile tile = bag.get(rand.nextInt(bag.size()));
        bag.remove(tile);

        return tile;
    }

    public void addPlayer(Player player)
    {
        players.add(player);

        assignTiles(player);

        if(players.size() == 1)
            curPlayer = players.get(0);
    }

    public QwirkleModel()
    {
        // Empty model;
        players = new ArrayList<>();

        fillBag();
        allTiles = new ArrayList<>();
        allTiles.addAll(bag);

        counter = 0;
        madeOneMove = false;
        latestMoveSet = new ArrayList<>();
    }
}
