package capstone.qwirkleclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import capstone.Tile;

@SuppressLint("AppCompatCustomView")
public class TextTile extends TextView {
    Tile tile;

    public TextTile(Context context, Tile tile) {
        super(context);

        setWidth(85);
        setHeight(85);
        setTextSize(30);

        setBackgroundColor(Color.LTGRAY);

        setTextAlignment(TEXT_ALIGNMENT_CENTER);

        setTile(tile);
    }

    public void setTile(Tile tile)
    {
        this.tile = tile;

        setBackgroundColor(Color.LTGRAY);

        if(tile != null)
            switch(tile.colour)
            {
                case RED:
                    setTextColor(Color.RED);
                    break;
                case ORANGE:
                    setTextColor(Color.parseColor("#FFA500"));
                    break;
                case YELLOW:
                    setTextColor(Color.YELLOW);
                    break;
                case GREEN:
                    setTextColor(Color.GREEN);
                    break;
                case BLUE:
                    setTextColor(Color.BLUE);
                    break;
                case PURPLE:
                    setTextColor(Color.parseColor("#A020F0"));
                    break;
            }

        String shape = "";

        if(tile != null)
            switch(tile.shape)
            {
                case CIRCLE:
                    shape = "\u25CF";
                    break;
                case CROSS:
                    shape = "\u2A09";
                    break;
                case DIAMOND:
                    shape = "\u25C6";
                    break;
                case SQUARE:
                    shape = "\u25A0";
                    break;
                case STAR:
                    shape = "\u2605";
                    break;
                case PLUS:
                    shape = "\u271A";
                    break;
            }

        setText(shape);
    }
}
