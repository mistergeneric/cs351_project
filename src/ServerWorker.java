import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

// ClientHandler class
class ServerWorker implements Runnable {
    Scanner scanner = new Scanner(System.in);
    private String name;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private boolean isloggedin;

    // constructor
    public ServerWorker(Socket s, String name,
                        DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = s;
        this.isloggedin = true;
    }

    ServerWorker() {
    }

    @Override
    public void run() {

        String received;
        while (true) {
            try {
                // receive the string
                received = dataInputStream.readUTF();

                System.out.println(received);

                if (received.contains("/logout")) {
                    this.isloggedin = false;
                    this.socket.close();
                    break;
                }
                //this is where we check if it is a message command
                else if (received.contains("/msg")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String MsgToSend = st.nextToken();
                    String recipient = st.nextToken();

                    for (ServerWorker mc : ServerListener.serverWorkerList) {
                        if (mc.name.equals(recipient) && mc.isloggedin) {
                            mc.dataOutputStream.writeUTF(this.name + " : " + MsgToSend);
                            break;
                        }
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try {
            // closing resources
            this.dataInputStream.close();
            this.dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}