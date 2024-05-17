package capstone.qwirkleclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import capstone.Player;
import capstone.QwirkleModel;
import capstone.Tile;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.client.GameOver;
import capstone.networking.messages.client.Quit;
import capstone.networking.messages.client.SendTurnFinishedMessage;
import capstone.networking.messages.client.WaitingForGame;
import capstone.networking.messages.server.EndGame;
import capstone.networking.messages.server.Left;
import capstone.networking.messages.server.SendModel;

public class NetworkedBoardActivity extends AppCompatActivity {
    QwirkleModel model;
    TileAdapter.TileViewHolder curView;
    TileAdapter adapter;
    RecyclerView recyclerTiles;
    TextView txtScore;
    TextView txtPlayer;
    boolean swapping;
    List<Tile> tilesToSwap;
    Button btnSwap, btnEnd;
    View.OnClickListener normalListener;
    TableLayout tblGrid;
    Tile.Position latestPosition;
    List<Tile.Position> possiblePlaces;
    boolean swapped;
    Player player;
    QwirkleClient client;
    boolean isMyTurn;
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

        isMyTurn = false;

        txtScore = findViewById(R.id.txtScore);
        txtPlayer = findViewById(R.id.txtPlayer);
        tblGrid = findViewById(R.id.tblGrid);
        btnEnd = findViewById(R.id.btnEnd);
        recyclerTiles = findViewById(R.id.recyclerTiles);

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

        // Setup swapping fields:
        swapping = false;
        swapped = false;
        tilesToSwap = new ArrayList<>();
        btnSwap = findViewById(R.id.btnSwap);
        btnSwap.setBackgroundColor(Color.BLUE);

        // Hide controls.
        showHideControls(View.GONE);

        // Get intent:
        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra("player");
        String serverAddress = intent.getStringExtra("serverAddress");

        // Create client.
        Log.i("QwirkleClient", "Connecting to " + serverAddress + " as " + player.handle);
        client = new QwirkleClient(
                message -> runOnUiThread(
                        () -> onMessageReceived(message)
                ));

        client.connect(serverAddress, player.handle);

        player.handle = client.handle;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            client.send(new WaitingForGame(player));
        }
    }

    private void showHideControls(int visibility) {
        btnSwap.setVisibility(visibility);
        btnEnd.setVisibility(visibility);
        recyclerTiles.setVisibility(visibility);
        tblGrid.setVisibility(visibility);
    }

    public void onSwapClicked(View view) {
        swapping = !swapping;

        tilesToSwap = new ArrayList<>();

        if (swapping) {
            Controller.swapping(true, btnSwap);

            adapter.setOnLongClickListener(v -> false);
            adapter.setOnClickListener(v -> {
                MediaPlayer.create(NetworkedBoardActivity.this, R.raw.swapping).start();

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
            adapter.setOnLongClickListener(longClickListener);
        }

        if(!(swapped || model.madeOneMove)) {
            MediaPlayer.create(NetworkedBoardActivity.this, R.raw.error).start();

            Toast.makeText(view.getContext(), getString(R.string.NothingDone), Toast.LENGTH_SHORT).show();
            possiblePlaces = new ArrayList<>();
            fillTable();
            return;
        }

        MediaPlayer.create(NetworkedBoardActivity.this, R.raw.endturn).start();

        btnSwap.setClickable(true);
        btnSwap.setBackgroundColor(Color.BLUE);

        possiblePlaces = new ArrayList<>();

        swapped = false;

        model.addScore();

        if(model.gameOver())
        {
            client.send(new GameOver(model));
        }

        model.nextPlayer();

        fillTable();

        Controller.updateScore(txtScore, player);
        txtPlayer.setText(new StringBuilder().append(model.curPlayer.handle).append(getString(R.string.turn)).toString());
        client.sendTurnFinishedMessage(model);
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
            TableRow row = new TableRow(NetworkedBoardActivity.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = y2; j <= y1; j++) {
                TextTile txtTile = new TextTile(NetworkedBoardActivity.this, model.getTile(i, j));
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
                                MediaPlayer.create(NetworkedBoardActivity.this, R.raw.placing).start();

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
                                    Controller.qwirkle(NetworkedBoardActivity.this, v);
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
                    MediaPlayer.create(NetworkedBoardActivity.this, R.raw.placing).start();

                    latestPosition = new Tile.Position(curX, curY);

                    if (curView != null && ((TextTile) v).tile == null && Controller.containsPosition(possiblePlaces, curX, curY)) {
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
                           Controller.qwirkle(NetworkedBoardActivity.this, v);
                        }
                        Controller.updateScore(txtScore, player);

                        client.send(new SendTurnFinishedMessage(model));
                    }
                });
                row.addView(txtTile);
            }
            table.addView(row);
        }
    }

    boolean setup = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onMessageReceived(Message msg) {
        if(msg instanceof SendModel)
        {
            model = ((SendModel) msg).model;

            showHideControls(View.VISIBLE);

            for(Player play3r : model.players)
                if(play3r.handle.equals(player.handle))
                    player = play3r;

            isMyTurn = model.curPlayer.handle.equals(player.handle);

            if(!setup)
            {
                setup = true;

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

                normalListener = v -> {
                    MediaPlayer.create(NetworkedBoardActivity.this, R.raw.selected).start();

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

                adapter = new TileAdapter(player.tiles, normalListener);
                adapter.setOnLongClickListener(longClickListener);

                RecyclerView.LayoutManager layoutManager;
                layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

                recyclerTiles.setLayoutManager(layoutManager);
                recyclerTiles.setAdapter(adapter);
            }

            Controller.updateScore(txtScore, player);
            txtPlayer.setText(new StringBuilder().append(model.curPlayer.handle).append(getString(R.string.turn)).toString());

            adapter = new TileAdapter(player.tiles, normalListener);
            recyclerTiles.setAdapter(adapter);
            fillTable();

            if(!isMyTurn)
            {
                btnEnd.setClickable(false);
                btnSwap.setClickable(false);
                adapter.setOnClickListener(v -> {});
            }
            else
            {
                btnEnd.setClickable(true);
                if(!model.madeOneMove)
                    btnSwap.setClickable(true);
                adapter.setOnClickListener(normalListener);
            }
        }

        if(msg instanceof Left)
        {
            Left left = (Left) msg;

            Toast.makeText(btnEnd.getContext(), left.handle + getString(R.string.Left), Toast.LENGTH_LONG).show();

            if(model.players.size() <= 2)
            {
                client.send(new Quit());
                Intent intent = new Intent(NetworkedBoardActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Player leftPlayer = model.players.stream().filter(player -> player.handle.equals(left.handle)).collect(Collectors.toList()).get(0);

                model.removePlayer(leftPlayer);

                client.send(new SendTurnFinishedMessage(model));
            }
        }

        if(msg instanceof EndGame)
        {
            EndGame endGame = (EndGame) msg;
            model = endGame.model;

           Controller.goToResults(NetworkedBoardActivity.this, model);
        }
    }

    @Override
    public void onBackPressed()
    {
        client.send(new Quit());
        Intent intent = new Intent(NetworkedBoardActivity.this, MainActivity.class);
        startActivity(intent);
    }
}