import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorker extends Thread {
    private Socket clientSocket;

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this is the 'meat and potatoes' so this is where we get the message or whatever else and then we'll process it
    private void handleClientSocket() throws IOException {
        
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write("Hello World\n".getBytes());
        clientSocket.close();
    }

}
