package MultiPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectServer implements Runnable, MultiplayerConstants {

    private static ConnectServer instance;
    public static volatile boolean connected = false;

    public static ConnectServer instance() {
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
            instance().listener.callback();
        } else {
            new Thread(instance()).start();
        }
    }

    public interface ConnectServerListener {
        void callback();

        void loading(boolean b);

        void message(String s);
    }

    private final static int SERVER_PORT = 7158;
    private static final String SERVER_IP_ADDRESS = "80.85.156.14";

    public static InetAddress ipAddress;

    private ConnectServerListener listener;

    private ConnectServer() { }

    public static ObjectOutputStream out;
    public static ObjectInputStream in;

    private void sendCommand(Object data, int command) throws IOException {
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
                        case GIVE_LOGIN_AND_PASS: {
                            connected = true;
                            listener.callback();
                            break;
                        }
                        case ACCOUNT_ALREADY_EXISTS: {
                            listener.loading(false);
                            listener.message("Аккаунт уже существует! Попробуйте войти");
                            break;
                        }
                        case ACCOUNT_CREATED: {
                            sendCommand(LobbyModel.myProfile, UPDATE_PROFILE);
                            listener.message("Поздравляем! Учетная запись создана");
                            break;
                        }
                        case UNCORRECTED_LOGIN_OR_PASSWORD: {
                            listener.loading(false);
                            listener.message("Неправильный логин или пароль!");
                            break;
                        }
                        case SET_PROFILE: {
                            LobbyModel.myProfile = (Profile) data;
                            listener.message("Вы вошли под ником " + LobbyModel.myProfile.nick);
                            break;
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}