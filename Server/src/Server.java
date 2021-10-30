import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private int serverPort;
    private List<ServerWorker> serverWorkers;

    public Server(int serverPort) {
        this.serverPort = serverPort;
        serverWorkers = new ArrayList<>();
    }

    public List<ServerWorker> getServerWorkers() {
        return serverWorkers;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            Socket clientSocket = null;
            try {
                if(serverSocket != null) {
                    clientSocket = serverSocket.accept();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ServerWorker serverWorker = new ServerWorker(this, clientSocket);
            serverWorkers.add(serverWorker);
            serverWorker.start();
        }
    }
}
