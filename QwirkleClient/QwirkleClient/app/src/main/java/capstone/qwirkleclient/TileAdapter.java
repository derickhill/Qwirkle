package capstone.qwirkleclient;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import capstone.Tile;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder>
{
    public static class TileViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtTile;
        ConstraintLayout layoutTile;
        Tile tile;

        public TileViewHolder(@NonNull View view)
        {
            super(view);

            txtTile = view.findViewById(R.id.txtTile);
            layoutTile = view.findViewById(R.id.layoutTile);
        }

        public void setTile(Tile tile)
        {
            this.tile = tile;

            layoutTile.setBackgroundColor(Color.WHITE);

            switch(tile.colour)
            {
                case RED:
                    txtTile.setTextColor(Color.RED);
                    break;
                case ORANGE:
                    txtTile.setTextColor(Color.parseColor("#FFA500"));
                    break;
                case YELLOW:
                    txtTile.setTextColor(Color.YELLOW);
                    break;
                case GREEN:
                    txtTile.setTextColor(Color.GREEN);
                    break;
                case BLUE:
                    txtTile.setTextColor(Color.BLUE);
                    break;
                case PURPLE:
                    txtTile.setTextColor(Color.parseColor("#A020F0"));
                    break;
            }

            String shape = "";

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

            txtTile.setText(shape);
            txtTile.setTextSize(50);
        }
    }

    private final List<Tile> tiles;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public TileAdapter(List<Tile> tiles, View.OnClickListener onClickListener)
    {
        this.tiles = tiles;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_tiles,
                        parent, false);

        TileViewHolder tvh = new TileViewHolder(view);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TileViewHolder holder, int position)
    {
        Tile tile = tiles.get(position);

        holder.setTile(tile);

        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount()
    {
        return tiles.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

        notifyDataSetChanged();
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;

        notifyDataSetChanged();
    }

    public void remove(Tile... tile)
    {
        for (Tile value : tile) {
            int index = tiles.indexOf(value);

            tiles.remove(value);

            notifyItemRemoved(index);
        }
    }
}
