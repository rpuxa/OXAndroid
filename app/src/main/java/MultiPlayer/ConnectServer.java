package MultiPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import static MultiPlayer.LobbyModel.*;

public class ConnectServer implements Runnable, MultiPlayerConstants {

    private static ConnectServer instance;
    public static volatile boolean connected = false;
    public static ConnectServerListenerLobby lobbyListener;

    private static ConnectServer instance() {
        if (instance == null) {
            instance = new ConnectServer();
        }
        return instance;
    }

    public static void setListener(ConnectServerListener listener) {
        instance().listener = listener;
    }

    public static void connect() {
        if (connected) {
            instance().listener.connectedCallback();
        } else {
            new Thread(instance()).start();
        }
    }

    public interface ConnectServerListener {
        void connectedCallback();

        void setLoading(boolean b);

        void showMessage(String s);

        void showLobby();
    }

    public interface ConnectServerListenerLobby {
        void updateList(ArrayList<Player> players);
    }

    private final static int SERVER_PORT = 7158;
    private static final String SERVER_IP_ADDRESS = "80.85.156.14";

    private static InetAddress ipAddress;

    private ConnectServerListener listener;

    private ConnectServer() { }

    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void sendCommand(Object data, int command) throws IOException {
        out.writeObject(new ServerCommand(data, command));
        out.flush();
    }


    @Override
    public void run() {
        try {
            if (ipAddress == null)
                ipAddress = InetAddress.getByName(SERVER_IP_ADDRESS);

            Socket socket;
            while (true) {
                try {
                    socket = new Socket(ipAddress, SERVER_PORT);
                    break;
                } catch (ConnectException ignored) {
                }
            }

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                try {
                    ServerCommand server_command = (ServerCommand) in.readObject();
                    Object data = server_command.getData();
                    switch (server_command.getCommand()) {
                        case GIVE_LOGIN_AND_PASS:
                            connected = true;
                            listener.connectedCallback();
                            break;
                        case ACCOUNT_ALREADY_EXISTS:
                            listener.setLoading(false);
                            listener.showMessage("Аккаунт уже существует! Попробуйте войти");
                            break;
                        case ACCOUNT_CREATED:
                            sendCommand(LobbyModel.myProfile, UPDATE_PROFILE);
                            listener.showMessage("Поздравляем! Учетная запись создана");
                            listener.showLobby();
                            waitForUpdateListenerLobby();
                            break;
                        case UNCORRECTED_LOGIN_OR_PASSWORD:
                            listener.setLoading(false);
                            listener.showMessage("Неправильный логин или пароль!");
                            break;
                        case SET_PROFILE:
                            LobbyModel.myProfile = (Profile) data;
                            listener.showMessage("Вы вошли под ником " + LobbyModel.myProfile.nick);
                            listener.showLobby();
                            waitForUpdateListenerLobby();
                            break;
                        case UPDATE_PLAYER_LIST:
                            lobbyListener.updateList((ArrayList<Player>) data);
                            break;
                        case SET_YOUR_ID:
                            myID = (long) data;
                            break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void waitForUpdateListenerLobby(){
        while (lobbyListener == null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }
}
