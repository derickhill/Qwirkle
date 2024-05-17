package capstone;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
    public Player winner;

    public QwirkleModel(Player... players)
    {
        this.players = Arrays.asList(players.clone());

        fillBag();
        allTiles = new ArrayList<>();
        allTiles.addAll(bag);

        for(Player player : players)
        {
            assignTiles(player);
        }

        //  CHANGE THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        curPlayer = players[0];

        counter = 0;
        madeOneMove = false;
        latestMoveSet = new ArrayList<>();
    }

    public void addPlayer(Player player)
    {
        players.add(player);

        for(Player curPlayer : players)
        {
            assignTiles(player);
        }

        //  CHANGE THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void nextPlayer()
    {
        assignTiles(curPlayer);

        int index = players.indexOf(curPlayer);

        if(index == players.size() - 1)
            curPlayer = players.get(0);
        else
            curPlayer = players.get(index + 1);

        madeOneMove = false;
        latestMoveSet = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addScore()
    {
        if(latestMoveSet.isEmpty())
            return;

        List<Tile> verticalLine, horizontalLine;
        int lineSize;

        verticalLine = getLine(latestMoveSet.get(0), true);
        horizontalLine = getLine(latestMoveSet.get(0), false);

        if(latestMoveSet.size() == 1) {
            if(verticalLine.size() > 1)
                curPlayer.score += verticalLine.size();

            if(horizontalLine.size() > 1)
                curPlayer.score += horizontalLine.size();

            return;
        }

        if(verticalLine.contains(latestMoveSet.get(1)))
        {
            curPlayer.score += verticalLine.size();

            for(Tile tile : latestMoveSet) {
                lineSize = getLine(tile, false).size();

                if(lineSize > 1)
                    curPlayer.score += lineSize;
            }
        }
        else {
            curPlayer.score += horizontalLine.size();

            for(Tile tile : latestMoveSet) {
                lineSize = getLine(tile, true).size();

                if(lineSize > 1)
                    curPlayer.score += lineSize;
            }
        }
    }

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

    public void swapPieces(List<Tile> tiles)
    {
        for(Tile tile : tiles)
        {
            curPlayer.tiles.remove(tile);
        }

        assignTiles(curPlayer);

        bag.addAll(tiles);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getMaxX()
    {
        int maxX = -1000;

        Optional<Tile> optional = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).max(Comparator.comparingInt(t -> t.position.x));

        if(optional.isPresent())
            maxX = optional.get().position.x;

        return maxX;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getMaxY()
    {
        int maxY = -1000;

        Optional<Tile> optional = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).max(Comparator.comparingInt(t -> t.position.y));

        if(optional.isPresent())
            maxY = optional.get().position.y;

        return maxY;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getMinX()
    {
        int minX = -1000;

        Optional<Tile> optional = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).min(Comparator.comparingInt(t -> t.position.x));

        if(optional.isPresent())
            minX = optional.get().position.x;

        return minX;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getMinY()
    {
        int minY = -1000;

        Optional<Tile> optional = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).min(Comparator.comparingInt(t -> t.position.y));

        if(optional.isPresent())
            minY = optional.get().position.y;

        return minY;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Tile getTile(int x, int y)
    {
        Tile tile = null;

        Optional<Tile> optional = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).filter(t -> t.position.x == x && t.position.y == y).findFirst();

        if(optional.isPresent())
            tile = optional.get();

        return tile;
    }

    public void placeTile(Tile curTile, Tile.Position latestPosition)
    {
        curTile.state = Tile.State.PLACED;
        curTile.position = latestPosition;

        latestMoveSet.add(curTile);
        madeOneMove = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Tile.Position> getPossiblePlaces(Tile tile)
    {
        List<Tile.Position> possiblePlaces = new ArrayList<>();

        List<Tile> placedTiles = allTiles.stream().filter(t -> t.state.equals(Tile.State.PLACED)).collect(Collectors.toList());

        List<Tile> line;

        if(counter == 0)
        {
            possiblePlaces.add(new Tile.Position(0, 0));
            return possiblePlaces;
        }

        if(!madeOneMove || counter < 2) {
            for (Tile placedTile : placedTiles) {
                line = getLine(placedTile, true);

                if (validShape(line, tile) || validColour(line, tile)) {
                    for (Tile curTile : line) {
                        Tile.Position pos = curTile.position;
                        int x = pos.x;
                        int y = pos.y;

                        if (getTile(x, y + 1) == null)
                            possiblePlaces.add(new Tile.Position(x, y + 1));

                        if (getTile(x, y - 1) == null)
                            possiblePlaces.add(new Tile.Position(x, y - 1));
                    }
                }

                line = getLine(placedTile, false);

                if (validShape(line, tile) || validColour(line, tile)) {
                    for (Tile curTile : line) {
                        Tile.Position pos = curTile.position;
                        int x = pos.x;
                        int y = pos.y;

                        if (getTile(x + 1, y) == null)
                            possiblePlaces.add(new Tile.Position(x + 1, y));

                        if (getTile(x - 1, y) == null)
                            possiblePlaces.add(new Tile.Position(x - 1, y));
                    }
                }
            }
        }

        if(counter >= 2)
        {
            if(latestMoveSet.size() == 1) {
                line = getLine(latestMoveSet.get(0), true);

                if (validShape(line, tile) || validColour(line, tile)) {
                    for (Tile curTile : line) {
                        Tile.Position pos = curTile.position;
                        int x = pos.x;
                        int y = pos.y;

                        if (getTile(x, y + 1) == null)
                            possiblePlaces.add(new Tile.Position(x, y + 1));

                        if (getTile(x, y - 1) == null)
                            possiblePlaces.add(new Tile.Position(x, y - 1));
                    }
                }

                line = getLine(latestMoveSet.get(0), false);

                if (validShape(line, tile) || validColour(line, tile)) {
                    for (Tile curTile : line) {
                        Tile.Position pos = curTile.position;
                        int x = pos.x;
                        int y = pos.y;

                        if (getTile(x + 1, y) == null)
                            possiblePlaces.add(new Tile.Position(x + 1, y));

                        if (getTile(x - 1, y) == null)
                            possiblePlaces.add(new Tile.Position(x - 1, y));
                    }
                }
            }

            if(latestMoveSet.size() > 1) {
                boolean vertical = false;

                line = getLine(latestMoveSet.get(0), true);

                if(line.contains(latestMoveSet.get(1)))
                    vertical = true;

                if(vertical)
                {
                    if (validShape(line, tile) || validColour(line, tile)) {
                        for (Tile curTile : line) {
                            Tile.Position pos = curTile.position;
                            int x = pos.x;
                            int y = pos.y;

                            if (getTile(x, y + 1) == null)
                                possiblePlaces.add(new Tile.Position(x, y + 1));

                            if (getTile(x, y - 1) == null)
                                possiblePlaces.add(new Tile.Position(x, y - 1));
                        }
                    }
                }
                else {
                    line = getLine(latestMoveSet.get(0), false);

                    if (validShape(line, tile) || validColour(line, tile)) {
                        for (Tile curTile : line) {
                            Tile.Position pos = curTile.position;
                            int x = pos.x;
                            int y = pos.y;

                            if (getTile(x + 1, y) == null)
                                possiblePlaces.add(new Tile.Position(x + 1, y));

                            if (getTile(x - 1, y) == null)
                                possiblePlaces.add(new Tile.Position(x - 1, y));
                        }
                    }
                }
            }
        }

        // Remove positions that are compatible with only vertical lines but not horizontal ones and vice versa.
        for (Tile placedTile : placedTiles) {
            line = getLine(placedTile, true);

            if (!(validShape(line, tile) || validColour(line, tile)) || (validShape(line, tile) && validColour(line, tile))) {
                for (Tile curTile : line) {
                    Tile.Position pos = curTile.position;
                    int x = pos.x;
                    int y = pos.y;

                    removePosition(possiblePlaces, x, y + 1);
                    removePosition(possiblePlaces, x, y - 1);
                }
            }

            line = getLine(placedTile, false);

            if (!(validShape(line, tile) || validColour(line, tile))  || (validShape(line, tile) && validColour(line, tile))) {
                for (Tile curTile : line) {
                    Tile.Position pos = curTile.position;
                    int x = pos.x;
                    int y = pos.y;

                    removePosition(possiblePlaces, x + 1, y);
                    removePosition(possiblePlaces, x - 1, y);
                }
            }
        }


        // Checking if I place tile in a current possible position, will it create a line that shouldn't exist?
        List<Tile.Position> positionsToRemove = new ArrayList<>();

        for(Tile.Position pos : possiblePlaces)
        {
            tile.position = pos;
            tile.state = Tile.State.PLACED;

            List<Tile> verticalLine, horizontalLine;

            verticalLine = getLine(tile, true);
            horizontalLine = getLine(tile, false);

            if(verticalLine.size() > 6 || horizontalLine.size() > 6)
                positionsToRemove.add(pos);
            else {
                for (Tile vertTile : verticalLine) {
                    if(!validColour(verticalLine, vertTile) && !validShape(verticalLine, vertTile))
                        positionsToRemove.add(pos);
                }

                for (Tile horTile : horizontalLine) {
                    if(!validColour(horizontalLine, horTile) && !validShape(horizontalLine, horTile))
                        positionsToRemove.add(pos);
                }
            }
            tile.position = null;
            tile.state = Tile.State.BAG;
        }

        for(Tile.Position pos : positionsToRemove)
            possiblePlaces.remove(pos);

        return possiblePlaces;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Tile> getLine(Tile tile, boolean vertical)
    {
        List<Tile> line = new ArrayList<>();
        Tile curTile = tile;

        int x = tile.position.x;
        int y = tile.position.y;

        if(vertical) {
            while(curTile != null) {
                line.add(curTile);

                curTile = getTile(x, --y);
            }

            curTile = tile;
            x = tile.position.x;
            y = tile.position.y;

            line.remove(tile);

            while(curTile != null) {
                line.add(curTile);
                curTile = getTile(x, ++y);
            }
        }
        else {
            while(curTile != null) {
                line.add(curTile);
                curTile = getTile(++x, y);
            }

            curTile = tile;
            x = tile.position.x;
            y = tile.position.y;

            line.remove(tile);

            while(curTile != null) {
                line.add(curTile);
                curTile = getTile(--x, y);
            }
        }
        return line;
    }

    // For line of tiles with same shape but different colour.
    public boolean validShape(List<Tile> line, Tile tile)
    {
        for(Tile curTile : line)
        {
            if(!tile.equals(curTile))
                if(!tile.shape.equals(curTile.shape) || tile.colour.equals(curTile.colour))
                    return false;
        }

        return true;
    }

    // For line of tiles with same colour but different shape.
    public boolean validColour(List<Tile> line, Tile tile)
    {
        for(Tile curTile : line)
        {
            if(!tile.equals(curTile))
                if(tile.shape.equals(curTile.shape) || !tile.colour.equals(curTile.colour))
                    return false;
        }

        return true;
    }

    public void removePosition(List<Tile.Position> possiblePlaces, int i, int j)
    {
        List<Tile.Position> toRemove = new ArrayList<>();

        if(possiblePlaces != null)
        {
            for (Tile.Position pos : possiblePlaces)
            {
                if(i == pos.x && j == pos.y)
                    toRemove.add(pos);
            }
        }

        for(Tile.Position pos : toRemove)
        {
            possiblePlaces.remove(pos);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getQwirkle(Tile tile)
    {
        List<Tile> line;
        boolean qwirkle = false;

        line = getLine(tile, true);

        if(line.size() == 6) {
            curPlayer.score += line.size();
            qwirkle = true;
        }

        line = getLine(tile, false);

        if(line.size() == 6) {
            curPlayer.score += line.size();
            qwirkle = true;
        }

        return qwirkle;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gameOver()
    {
        if(bag.isEmpty() && curPlayer.tiles.isEmpty()) {
            curPlayer.score += 6;
            winner = players.stream().max(Comparator.comparingInt(player -> player.score)).get();
            return true;
        }

        latestMoveSet = new ArrayList<>();
        madeOneMove = false;

        List<Tile> unplaced = allTiles.stream().filter(t -> t.state.equals(Tile.State.BAG)).collect(Collectors.toList());

        for(Tile tile : unplaced)
        {
            if(!getPossiblePlaces(tile).isEmpty())
            {
                return false;
            }
        }

        winner = players.stream().max(Comparator.comparingInt(player -> player.score)).get();

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removePlayer(Player player)
    {
        List<Tile> playerTiles = player.tiles;

        players.remove(player);

        bag.addAll(playerTiles);

        if(player.equals(curPlayer))
            nextPlayer();
    }
}
