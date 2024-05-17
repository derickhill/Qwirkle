package capstone;

import java.io.Serializable;

public class Tile implements Serializable
{
    private static final long serialVersionUID = 273L;

    public enum Shape {
        CIRCLE, CROSS, DIAMOND, SQUARE, STAR, PLUS
    }

    public enum Colour {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE
    }

    public enum State {
        PLACED, BAG
    }

    public static class Position implements Serializable {
        private static final long serialVersionUID = 5656L;
        public int x;
        public int y;

        public Position(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public Shape shape;
    public Colour colour;
    public Position position;
    public State state;

    public Tile(Shape shape, Colour colour, Position position, State state)
    {
        this.shape = shape;
        this.colour = colour;
        this.position = position;
        this.state = state;
    }

    public Tile()
    {
        shape = null;
        colour = null;
        position = null;
        state = State.BAG;
    }
}
