package capstone.qwirkleclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import capstone.Player;
import capstone.QwirkleModel;

public class ResultsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();

        QwirkleModel model = (QwirkleModel) intent.getSerializableExtra("model");

        TextView txtP1 = findViewById(R.id.txtP1);
        TextView txtP2 = findViewById(R.id.txtP2);
        TextView txtP3 = findViewById(R.id.txtP3);
        TextView txtP4 = findViewById(R.id.txtP4);

        List<Player> players = model.players;

        players.sort((p1, p2) -> Integer.compare(p2.score, p1.score));

        if(players.size() > 0)
            txtP1.setText(new StringBuilder().append(players.get(0).handle).append("\t\t").append(getString(R.string.Score)).append(": ").append(players.get(0).score).toString());

        if(players.size() > 1)
            txtP2.setText((new StringBuilder().append(players.get(1).handle).append("\t\t").append(getString(R.string.Score)).append(": ").append(players.get(1).score).toString()));

        if(players.size() > 2)
            txtP3.setText((new StringBuilder().append(players.get(2).handle).append("\t\t").append(getString(R.string.Score)).append(": ").append(players.get(2).score).toString()));
        else
            txtP3.setText("");

        if(players.size() > 3)
            txtP4.setText((new StringBuilder().append(players.get(3).handle).append("\t\t").append(getString(R.string.Score)).append(": ").append(players.get(3).score).toString()));
        else
            txtP4.setText("");

       MediaPlayer.create(ResultsActivity.this, R.raw.hooray).start();

    }

    public void onHomeClicked(View view)
    {
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}