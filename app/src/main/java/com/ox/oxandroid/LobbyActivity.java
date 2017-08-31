package com.ox.oxandroid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import MultiPlayer.ConnectServer;
import MultiPlayer.LobbyModel;
import MultiPlayer.Player;
import MultiPlayer.Profile;

import static MultiPlayer.LobbyModel.*;

public class LobbyActivity extends AppCompatActivity implements ConnectServer.ConnectServerListenerLobby {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectServer.lobbyListener = this;
        runOnUiThread(() -> {
            super.onCreate(savedInstanceState);
            TextView yourNick = (TextView) findViewById(R.id.your_profile_nick)
                    , yourDate = (TextView) findViewById(R.id.your_profile_date)
                    , yourWins = (TextView) findViewById(R.id.your_profile_wins)
                    , yourDraws = (TextView) findViewById(R.id.your_profile_draws)
                    , yourLoses = (TextView) findViewById(R.id.your_profile_loses);
            yourNick.setText("" + myProfile.nick);
            yourDate.setText(myProfile.date_registration);
            String st_wins = "" + myProfile.wins, st_draws = "" + myProfile.draws, st_loses = "" + myProfile.loses;
            yourWins.setText(st_wins);
            yourDraws.setText(st_draws);
            yourLoses.setText(st_loses);
            setContentView(R.layout.lobby);
        });
    }

    @Override
    public void updateList(ArrayList<Player> players) {
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
                String st_rating = player.getRating() + "";
                rating.setText(st_rating);
                TextView invite = new TextView(this);
                invite.setTextSize(18);
                invite.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
                invite.setText("пригласить");
                if (player.getId() == LobbyModel.myID)
              ;//      invite.setVisibility(View.INVISIBLE);
                invite.setTextColor(Color.BLUE);
                TextView nick = new TextView(this);
                nick.setTextSize(18);
                nick.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                String st_nick = "     " + player.getName();
                nick.setText(st_nick);
                row.addView(rating);
                row.addView(nick);
                row.addView(invite);
                mainLayout.addView(row);
            }
        });
    }

    @Override
    public void openProfile(Profile profile){

    }
}
