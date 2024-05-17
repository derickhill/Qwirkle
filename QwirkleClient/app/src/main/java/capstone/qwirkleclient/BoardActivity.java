package capstone.qwirkleclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import capstone.Player;
import capstone.QwirkleModel;
import capstone.Tile;

public class BoardActivity extends AppCompatActivity {
    QwirkleModel model;
    TileAdapter.TileViewHolder curView;
    TileAdapter adapter;
    RecyclerView recyclerTiles;
    TextView txtScore, txtPlayer;
    List<Tile> tilesToSwap;
    Button btnSwap;
    View.OnClickListener normalListener;
    TableLayout tblGrid;
    Tile.Position latestPosition;
    List<Tile.Position> possiblePlaces;
    boolean swapped, swapping;
    View.OnLongClickListener longClickListener;
    private TableRow.LayoutParams layoutParams;
    View latestHovered;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        ConstraintLayout layout = findViewById(R.id.layout);

        layout.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                if (curView != null)
                    curView.txtTile.setVisibility(View.VISIBLE);
            }

            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                if(latestHovered != null)
                    latestHovered.setBackgroundColor(Color.DKGRAY);
            }
                else
                    fillTable();
            return true;
        });

        txtScore = findViewById(R.id.txtScore);
        txtPlayer = findViewById(R.id.txtPlayer);

        // Setup swapping fields:
        swapping = false;
        swapped = false;
        tilesToSwap = new ArrayList<>();
        btnSwap = findViewById(R.id.btnSwap);
        btnSwap.setBackgroundColor(Color.BLUE);

        // Get intent:
        Player[] players = (Player[]) getIntent().getSerializableExtra("Players");

        // Create game model:
        model = new QwirkleModel(players);

        // Setup grid:
        tblGrid = findViewById(R.id.tblGrid);
        fillTable();

        // Setup recycler view:
        normalListener = v -> {
            MediaPlayer.create(BoardActivity.this, R.raw.selected).start();

            TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) recyclerTiles.findContainingViewHolder(v);

            Tile tile = viewHolder.tile;

            if (curView != null) {
                curView.layoutTile.setBackgroundColor(Color.WHITE);
            }

            curView = viewHolder;
            curView.layoutTile.setBackgroundColor(Color.GRAY);

            possiblePlaces = model.getPossiblePlaces(tile);

            fillTable();
        };

        longClickListener = (View.OnLongClickListener) v -> {
            TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) recyclerTiles.findContainingViewHolder(v);

            Tile tile = viewHolder.tile;

            if (curView != null) {
                curView.layoutTile.setBackgroundColor(Color.WHITE);
            }

            curView = viewHolder;
            curView.layoutTile.setBackgroundColor(Color.GRAY);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(curView.itemView);

            curView.itemView.startDrag(data, shadowBuilder, curView.itemView, 0);


            possiblePlaces = model.getPossiblePlaces(tile);

            fillTable();

            curView.txtTile.setVisibility(View.INVISIBLE);

            ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData dragData = new ClipData("",mimeTypes, item);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(curView.itemView);

            v.startDrag(dragData,myShadow,null,0);
            return true;
        };

        recyclerTiles = findViewById(R.id.recyclerTiles);

        adapter = new TileAdapter(model.curPlayer.tiles, normalListener);
        adapter.setOnLongClickListener(longClickListener);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerTiles.setLayoutManager(layoutManager);
        recyclerTiles.setAdapter(adapter);

        recyclerTiles.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                if (curView != null)
                    curView.txtTile.setVisibility(View.VISIBLE);
            }

            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                if(latestHovered != null)
                    latestHovered.setBackgroundColor(Color.DKGRAY);
            }
            else
                fillTable();
            return true;
        });

        Controller.updateScore(txtScore, model.curPlayer);
        txtPlayer.setText(new StringBuilder().append(model.curPlayer.handle).append(getString(R.string.turn)).toString());
    }

    public void onSwapClicked(View view) {
        swapping = !swapping;

        tilesToSwap = new ArrayList<>();

        if (swapping) {
            Controller.swapping(true, btnSwap);

            adapter.setOnLongClickListener(v -> false);
            adapter.setOnClickListener(v -> {
                MediaPlayer.create(BoardActivity.this, R.raw.swapping).start();

                TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) recyclerTiles.findContainingViewHolder(v);

                Tile tile = viewHolder.tile;

                if (tilesToSwap.contains(tile)) {
                    tilesToSwap.remove(tile);
                    viewHolder.layoutTile.setBackgroundColor(Color.WHITE);
                } else {
                    tilesToSwap.add(tile);
                    viewHolder.layoutTile.setBackgroundColor(Color.GRAY);
                }
            });
        }
        else
        {
            Controller.swapping(false, btnSwap);

            adapter.setOnClickListener(normalListener);
            adapter.setOnLongClickListener(longClickListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onEndTurnClicked(View view) {
        if(curView != null)
            curView.layoutTile.setBackgroundColor(Color.WHITE);
        curView = null;

        if (swapping) {
            adapter.setOnClickListener(normalListener);
            adapter.remove(tilesToSwap.toArray(new Tile[0]));

            if(tilesToSwap.size() > 0)
                swapped = true;

            model.swapPieces(tilesToSwap);

            tilesToSwap = new ArrayList<>();
            swapping = false;
            Controller.swapping(false, btnSwap);
        }

        if(!(swapped || model.madeOneMove)) {
            MediaPlayer.create(BoardActivity.this, R.raw.error).start();

            Toast.makeText(view.getContext(), getString(R.string.NothingDone), Toast.LENGTH_SHORT).show();
            possiblePlaces = new ArrayList<>();
            fillTable();
            return;
        }

        MediaPlayer.create(BoardActivity.this, R.raw.endturn).start();

        btnSwap.setClickable(true);
        btnSwap.setBackgroundColor(Color.BLUE);

        model.addScore();

        if(model.gameOver())
        {
            Toast.makeText(view.getContext(), model.winner.handle + " " + getString(R.string.Wins) + "!", Toast.LENGTH_LONG).show();

            // Go to results activity.
            Controller.goToResults(BoardActivity.this, model);
        }

        model.assignTiles(model.curPlayer);

        model.nextPlayer();
        Controller.updateScore(txtScore, model.curPlayer);
        txtPlayer.setText(new StringBuilder().append(model.curPlayer.handle).append(getString(R.string.turn)).toString());

        adapter = new TileAdapter(model.curPlayer.tiles, normalListener);
        adapter.setOnLongClickListener(longClickListener);
        recyclerTiles.setAdapter(adapter);
        possiblePlaces = null;

        swapped = false;
        fillTable();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillTable() {
        int x1, x2, y1, y2;

        TableLayout table = tblGrid;

        if (model.counter == 0) {
            x1 = 0;
            x2 = 0;
            y1 = 0;
            y2 = 0;
        } else {
            x1 = model.getMaxX() + 1;
            x2 = model.getMinX() - 1;
            y1 = model.getMaxY() + 1;
            y2 = model.getMinY() - 1;
        }

        table.removeAllViews();

        for (int i = x2; i <= x1; i++) {
            TableRow row = new TableRow(BoardActivity.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = y2; j <= y1; j++) {
                TextTile txtTile = new TextTile(BoardActivity.this, model.getTile(i, j));
                txtTile.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                if (Controller.containsPosition(possiblePlaces, i, j))
                {
                    txtTile.setBackgroundColor(Color.DKGRAY);
                }

                final int curX = i;
                final int curY = j;

                txtTile.setOnDragListener((v, event) -> {
                    switch(event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            layoutParams = (TableRow.LayoutParams)v.getLayoutParams();
                            // Do nothing
                            break;

                        case DragEvent.ACTION_DRAG_ENTERED:
                            int x_cord = (int) event.getX();
                            int y_cord = (int) event.getY();
                            break;

                        case DragEvent.ACTION_DRAG_EXITED :
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            layoutParams.leftMargin = x_cord;
                            layoutParams.topMargin = y_cord;
                            v.setLayoutParams(layoutParams);
                            break;

                        case DragEvent.ACTION_DRAG_LOCATION  :
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            if (curView != null && ((TextTile) v).tile == null && Controller.containsPosition(possiblePlaces, curX, curY)) {
                                ((TextTile) v).setBackgroundColor(Color.GREEN);
                                latestHovered = v;
                            }
                            else
                                if(latestHovered != null)
                                    latestHovered.setBackgroundColor(Color.DKGRAY);
                            break;

                        case DragEvent.ACTION_DROP   :
                            latestPosition = new Tile.Position(curX, curY);

                            if (curView != null && ((TextTile) v).tile == null && Controller.containsPosition(possiblePlaces, curX, curY)) {
                                MediaPlayer.create(BoardActivity.this, R.raw.placing).start();

                                Tile curTile = curView.tile;

                                ((TextTile) v).setTile(curTile);

                                model.placeTile(curTile, latestPosition);

                                model.counter++;

                                adapter.remove(curTile);
                                curView.txtTile.setVisibility(View.VISIBLE);

                                btnSwap.setClickable(false);
                                btnSwap.setBackgroundColor(Color.DKGRAY);

                                curView = null;
                                possiblePlaces = new ArrayList<>();

                                if(model.getQwirkle(curTile)) {
                                    Controller.qwirkle(BoardActivity.this, v);
                                }
                                Controller.updateScore(txtScore, model.curPlayer);
                            }
                            else
                            {
                                if(curView != null)
                                    curView.txtTile.setVisibility(View.VISIBLE);
                            }

                            fillTable();
                            break;

                        default: break;
                    }
                    return true;
                });

                txtTile.setOnClickListener(v -> {
                    latestPosition = new Tile.Position(curX, curY);

                    if (curView != null && ((TextTile) v).tile == null && Controller.containsPosition(possiblePlaces, curX, curY)) {
                        MediaPlayer.create(BoardActivity.this, R.raw.placing).start();

                        Tile curTile = curView.tile;

                        ((TextTile) v).setTile(curTile);

                        model.placeTile(curTile, latestPosition);

                        model.counter++;

                        adapter.remove(curTile);

                        btnSwap.setClickable(false);
                        btnSwap.setBackgroundColor(Color.DKGRAY);

                        curView = null;
                        possiblePlaces = new ArrayList<>();

                        fillTable();

                        if(model.getQwirkle(curTile)) {
                            Controller.qwirkle(BoardActivity.this, v);
                        }
                        Controller.updateScore(txtScore, model.curPlayer);
                    }
                });
                row.addView(txtTile);
            }
            table.addView(row);
        }
    }
}