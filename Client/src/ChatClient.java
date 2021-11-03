import com.sun.deploy.util.StringUtils;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();


    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        if(!client.connect()){
            System.err.println("Connection failed");
        }else{
            System.out.println("Connection successful");
            
            client.addUserStatusListener(new UserStatusListener(){
                @Override
                public void online(String login){
                    System.out.println("ONLINE: " + login);
                }
                @Override
                public void offline(String login){
                    System.out.println("OFFLINE: " + login);
                }
            });

            client.addMessageListener(new MessageListener() {
                @Override
                public void onMessage(String from, String message) {
                    System.out.println(from + " >>> " + message + "___________\n");
                }
            });

            if(client.login("guest", "guest")){
                System.out.println("Login Successful");
                client.msg("andrew", "Hello");
            }else{
                System.out.println("Login failed");
            }
            //client.logoff();
        }
    }

    private void msg(String recipient, String message) throws IOException {
        String cmd = "msg " + recipient + " " + message + "\n";
        serverOut.write(cmd.getBytes());
    }

    private void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    private boolean login(String id, String password) throws IOException {
        String cmd = "login " + id + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println(response);

        if ("Success".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        }else{
            return false;
        }

    }

    private void startMessageReader() {
        Thread thread = new Thread(){
            @Override
            public void run(){
                readMessageLoop();
            }
        };
        thread.start();
    }


    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];
                    if("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    }else if("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    }else if("msg".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ");
                        String message = String.join(" ", Arrays.copyOfRange(tokensMsg, 2, tokensMsg.length));
                        handleMessage(tokensMsg[1], message);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try{
                socket.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }

        }
    }

    private void handleMessage(String from, String message) {
        for(MessageListener listener : messageListeners){
            listener.onMessage(from, message);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) throws IOException {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    private boolean connect() throws IOException {
        try{
            socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }
}
