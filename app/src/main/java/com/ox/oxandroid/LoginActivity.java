package com.ox.oxandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import MultiPlayer.ConnectServer;
import MultiPlayer.Profile;
import MultiPlayer.ServerCommand;

public class LoginActivity extends AppCompatActivity implements ConnectServer.ConnectServerListener {

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
    public void callback() {
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
        TextView label = (TextView) findViewById(R.id.label_passwd1);

        enter.setOnClickListener(view -> {
            pass1.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            newAccount[0] = false;
        });

        create.setOnClickListener(view -> {
            pass1.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
            newAccount[0] = true;
        });

        accept.setOnClickListener(view -> {
            if (newAccount[0]){
                if (log.getText().length() < 4 || log.getText().length() > 16) {
                    message("Логин может быть не больше 16 символов и не меньше 4");
                    return;
                }
                char[] charsPass0 = pass0.getText().toString().toCharArray();
                char[] charsPass1 = pass1.getText().toString().toCharArray();
                if (!Profile.sameBytes(charsPass1,charsPass0)){
                    message("Не совпадают пароли");
                    return;
                }
                if (charsPass0.length < 4) {
                    message("Пароль слишком мленький");
                    return;
                }
                LobbyActivity.myProfile = Profile.make_new_profile(log.getText().toString());
                try {
                    ConnectServer.out.writeObject(new ServerCommand(Profile.loginPlusPass(log.getText().toString(),charsPass0),ConnectServer.CREATE_NEW_ACCOUNT));
                    ConnectServer.out.flush();
                    loading(true);
                } catch (IOException e) {
                }
            } else {
                if (log.getText().length() < 4 || log.getText().length() > 16) {
                    message("Логин может быть не больше 16 символов и не меньше 4");
                    return;
                }
                char[] charsPass0 = pass0.getText().toString().toCharArray();

                if (charsPass0.length < 4) {
                    message("Пароль слишком мленький");
                    return;
                }
                try {
                    ConnectServer.out.writeObject(new ServerCommand(Profile.loginPlusPass(log.getText().toString(),charsPass0),ConnectServer.LOGIN));
                    ConnectServer.out.flush();
                    loading(true);
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void loading(boolean b){
        findViewById(R.id.radio_enter).setEnabled(b);
        findViewById(R.id.radio_new).setEnabled(b);
        findViewById(R.id.log).setEnabled(b);
        findViewById(R.id.passwd0).setEnabled(b);
        findViewById(R.id.passwd1).setEnabled(b);
        findViewById(R.id.aceept).setVisibility(!b ? View.VISIBLE : View.GONE);
        findViewById(R.id.loading).setVisibility(b ? View.VISIBLE : View.GONE);
        findViewById(R.id.label_passwd1).setEnabled(b);
    }

    @Override
    public void message(String m){
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }
}


