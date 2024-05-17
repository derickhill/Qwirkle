package capstone;

import java.io.Serializable;

public class Tile implements Serializable
{
    private static final long serialVersionUID = 273L;
    enum Shape {
        CIRCLE, CROSS, DIAMOND, SQUARE, STAR, PLUS
    }

    enum Colour {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE
    }

    enum State {
        PLACED, BAG
    }

    static class Position implements Serializable {
        private static final long serialVersionUID = 5656L;
        int x, y;

        public Position(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    Shape shape;
    Colour colour;
    Position position;
    State state;

    public Tile()
    {
        shape = null;
        colour = null;
        position = null;
        state = State.BAG;
    }
}
