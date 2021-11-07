package user;

import java.io.IOException;
import java.net.Socket;

public class AdminUser extends User {

    Socket socket;

    public AdminUser(String login, String password, String description) {
        super(login, password, description);
        isAdmin = true;
    }

    public void editDescription(User user, String description){
        user.setDescription(description);
    }

    public void kickUser(User user) throws IOException {
        user.chatClient.logoff();
    }

    public void broadcastMessage(String message, String serverName, int serverPort) throws IOException {
        socket = new Socket(serverName, serverPort);
    }
}
