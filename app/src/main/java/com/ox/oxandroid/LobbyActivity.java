package com.ox.oxandroid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import MultiPlayer.ConnectServer;
import MultiPlayer.Player;

public class LobbyActivity extends AppCompatActivity implements ConnectServer.ConnectServerListenerLobby {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectServer.lobbyListener = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
    }


    @Override
    public void updateList(ArrayList<Player> players) {
        System.out.println();
        runOnUiThread(() -> {
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_lobby_layout);
            mainLayout.removeAllViews();
            for (Player player : players) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                TextView rating = new TextView(this);
                rating.setTextSize(18);
                rating.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
                rating.setText(player.getRating());
                TextView invite = new TextView(this);
                invite.setTextSize(18);
                invite.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
                invite.setText("пригласить");
                invite.setTextColor(Color.BLUE);
                TextView nick = new TextView(this);
                nick.setTextSize(18);
                nick.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                nick.setText("     " + player.getName());
                row.addView(rating);
                row.addView(nick);
                row.addView(invite);
                mainLayout.addView(row);
            }
        });
    }
}
