package com.ox.oxandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import MultiPlayer.ConnectServer;
import MultiPlayer.LobbyModel;
import MultiPlayer.MultiplayerConstants;
import MultiPlayer.Profile;

public class LoginActivity extends AppCompatActivity implements ConnectServer.ConnectServerListener, MultiplayerConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectServer.setListener(this);

        if (!ConnectServer.connected) {
            setContentView(R.layout.connecting);
            initConnectingActivity();
        } else {
            setContentView(R.layout.login);
            initLoginActivity();
        }
    }

    private void initConnectingActivity() {
        ConnectServer.connect();
    }

    @Override
    public void connectedCallback() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void initLoginActivity() {
        final boolean[] newAccount = {false};
        RadioButton enter = (RadioButton) findViewById(R.id.radio_enter);
        RadioButton create = (RadioButton) findViewById(R.id.radio_new);

        TextView log = (TextView) findViewById(R.id.log),
                pass0 = (TextView) findViewById(R.id.passwd0),
                pass1 = (TextView) findViewById(R.id.passwd1);

        Button accept = (Button) findViewById(R.id.aceept);
        TextView labelPasswd1 = (TextView) findViewById(R.id.label_passwd1);

        enter.setOnClickListener(view -> {
            pass1.setVisibility(View.GONE);
            labelPasswd1.setVisibility(View.GONE);
            newAccount[0] = false;
        });

        create.setOnClickListener(view -> {
            pass1.setVisibility(View.VISIBLE);
            labelPasswd1.setVisibility(View.VISIBLE);
            newAccount[0] = true;
        });

        accept.setOnClickListener(view -> {
            if (log.getText().length() < 4 || log.getText().length() > 16) {
                showMessage("Логин может быть не больше 16 символов и не меньше 4");
                return;
            }

            char[] charsPass0 = pass0.getText().toString().toCharArray();
            if (charsPass0.length < 4) {
                showMessage("Пароль слишком мленький");
                return;
            }

            int command = NOT_COMMAND;

            if (newAccount[0]){
                char[] charsPass1 = pass1.getText().toString().toCharArray();
                if (!Profile.sameBytes(charsPass1,charsPass0)){
                    showMessage("Не совпадают пароли");
                    return;
                }

                LobbyModel.myProfile = Profile.make_new_profile(log.getText().toString());
                command = CREATE_NEW_ACCOUNT;
            } else {
                command = LOGIN;
            }

            try {
                Object profile = Profile.loginPlusPass(log.getText().toString(),charsPass0);
                ConnectServer.sendCommand(profile, command);
                setLoading(true);
            } catch (IOException e) {
            }
        });
    }

    @IdRes
    final int[] enablindIds = {
            R.id.radio_enter, R.id.radio_new,
            R.id.log, R.id.passwd0, R.id.passwd1,
            R.id.label_passwd1
    };

    private void setVisibility(@IdRes int id, boolean visible) {
        findViewById(id).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLoading(boolean loading){
        runOnUiThread(() -> {
            for (int enablingId : enablindIds) {
                findViewById(enablingId).setEnabled(!loading);
            }

            setVisibility(R.id.aceept, !loading);
            setVisibility(R.id.loading, loading);
        });
    }

    @Override
    public void showMessage(String message){
        runOnUiThread(() -> {
            Toast.makeText(
                    getApplicationContext(),
                    message,
                    Toast.LENGTH_SHORT)
                .show();
        });
    }
}


