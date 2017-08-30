package MultiPlayer;

import java.io.Serializable;

public class ServerCommand implements Serializable {
    private Object data;
    private int command;

    public static final int CHECK_CONNECTION = 0;
    public static final int GIVE_LOGIN_AND_PASS = 1;
    public static final int SET_PROFILE = 2;
    public static final int CREATE_NEW_ACCOUNT = 3;
    public static final int LOGIN = 4;
    public static final int ACCOUNT_ALREADY_EXISTS = 5;
    public static final int UNCORRECTED_LOGIN_OR_PASSWORD = 6;
    public static final int ACCOUNT_CREATED = 7;
    public static final int UPDATE_PROFILE = 8;
    public static final int START_SEARCH_OPPONENT = 9;
    public static final int BEGIN_GAME_FOR_WHITE = 10;
    public static final int BEGIN_GAME_FOR_BLACK = 11;
    public static final int LOST_CONNECTION = 12;
    public static final int SET_OPPONENT_PROFILE = 13;

    public ServerCommand(Object data, int command) {
        this.data = data;
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public int getCommand() {
        return command;
    }
}