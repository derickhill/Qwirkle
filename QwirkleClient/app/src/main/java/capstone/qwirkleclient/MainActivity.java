package capstone.qwirkleclient;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import capstone.Player;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.Joined;

public class MainActivity extends AppCompatActivity {

    private QwirkleClient client;
    EditText txtHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner dropdown = findViewById(R.id.dropdown);

        String[] items = new String[]{"2", "3", "4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);

        txtHandle = findViewById(R.id.txtHandle);
    }

    public void onLocalClicked(View view)
    {
        Player.num = 1;

        Spinner dropdown = findViewById(R.id.dropdown);

        int numPlayers = Integer.parseInt(dropdown.getSelectedItem().toString());

        Player[] players = new Player[numPlayers];

        for(int i = 0; i < numPlayers; i++)
            players[i] = new Player(MainActivity.this);

        Intent intent = new Intent(MainActivity.this, BoardActivity.class);
        intent.putExtra("Players", players);

        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onConnectClicked(View view)
    {
        Player.num = 1;

        boolean hasPermission =
                checkSelfPermission(Manifest.permission.INTERNET) ==
                        PackageManager.PERMISSION_GRANTED;

        EditText edtServerAddress = findViewById(R.id.edtServerAddress);
        String serverAddress = edtServerAddress.getText().toString();

        String handle = "";

        Log.i("QwirkleClient", "Connecting to " + serverAddress + " as " + handle);
        client = new QwirkleClient(
                message -> runOnUiThread(
                        () -> onMessageReceived(message)
                ));

            client.connect(serverAddress, handle);
    }

    public void onMessageReceived(Message msg) {
        if(msg instanceof Joined)
        {
            if(((Joined) msg).groupName.equals("lobby")) {
                client.disconnect();

                Player player;

                EditText edtServerAddress = findViewById(R.id.edtServerAddress);
                String serverAddress = edtServerAddress.getText().toString();

                String handle = txtHandle.getText().toString();
                if(handle.equals(""))
                    player = new Player(MainActivity.this);
                else
                    player = new Player(handle);

                Intent intent = new Intent(MainActivity.this, NetworkedBoardActivity.class);

                intent.putExtra("player", player);
                intent.putExtra("serverAddress", serverAddress);
                startActivity(intent);
            }
        }
    }


}