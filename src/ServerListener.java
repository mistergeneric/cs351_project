import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListener {

    private int connectedClients;
    private int serverSocketPort;

    public static List<ServerWorker> serverWorkerList = new ArrayList<>();

    public ServerListener(int port) {
        this.connectedClients = 0;
        this.serverSocketPort = port;
    }

    public void serverStart() throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverSocketPort);
        while (true)
        {
            Socket socket = serverSocket.accept();

            System.out.println("New client request received : " + socket);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());


            ServerWorker serverWorker = new ServerWorker(socket,"client " + connectedClients, dataInputStream, dataOutputStream);

            Thread t = new Thread(serverWorker);


            serverWorkerList.add(serverWorker);

            t.start();
            connectedClients++;
        }
    }
}
