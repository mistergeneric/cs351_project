import java.io.IOException;

public class ChatRoomServerMain {


    public static void main(String[] args) throws IOException {
        ServerListener serverListener = new ServerListener(8818);
        serverListener.serverStart();
    }
}
