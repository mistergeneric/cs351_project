import java.io.IOException;
import java.net.Socket;

public class AdminObject extends UserObject {

    Server server;
    Socket socket;

    public AdminObject(String id, String password){
        super(id, password);

    }

    public void editDescription(UserObject user, String description){
        user.setDescription(description);
    }

    public void kickUser(UserObject user) throws IOException {
        user.chatClient.logoff();
    }

    public void broadcastMessage(String message, String serverName, int serverPort) throws IOException {
        socket = new Socket(serverName, serverPort);

    }
}
