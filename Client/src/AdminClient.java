import java.io.IOException;
import java.net.Socket;

public class AdminClient extends UserClient{

    Server server;
    Socket socket;

    public AdminClient(String id, String password){
        super(id, password);

    }

    public void editDescription(UserClient user, String description){
        user.setDescription(description);
    }

    public void kickUser(UserClient user) throws IOException {
        user.chatClient.logoff();
    }

    public void broadcastMessage(String message, String serverName, int serverPort) throws IOException {
        socket = new Socket(serverName, serverPort);

    }
}
