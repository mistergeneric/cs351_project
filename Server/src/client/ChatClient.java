package client;

import client.MessageListener;
import client.UserStatusListener;
import user.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    private BufferedWriter bufferedOut;
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private User user;
    private PrintWriter printWriter;
    private String login;
    private String filePath;

    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
        filePath = "usersFile.txt";
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        Scanner scanner = new Scanner(System.in);

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
                    System.out.println(from + " " + message );
                }
            });

            //System.out.println("Enter your ID: ");
            String id = "";
            String password = "";
            client.userInput();
            /*if(scanner.hasNextLine()){
                id = scanner.nextLine();
            }
            System.out.println("Enter your password: ");
            if(scanner.hasNextLine()){
                password = scanner.nextLine();
            }

            if(client.login(id, password)){
                System.out.println("Login Successful");
                client.userInput();
                client.msg("andrew", "Hello");
            }else{
                System.out.println("Login failed");
            }*/
            //client.logoff();
        }
    }

    private void userInput() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String s = scanner.nextLine() + "\n";
            serverOut.write(s.getBytes());
        }
    }

    void msg(String recipient, String message) throws IOException {
        String cmd = "msg " + recipient + " " + message + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
        user.SaveToFile(filePath);
    }

    protected boolean login(String id, String password) throws IOException {
        String cmd = "login " + id + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println(response);

        if ("Success".equalsIgnoreCase(response)){
            startMessageReader();
            user = new User(id, password);
            user.setChatClient(this);
            return true;
        } else {
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


    protected void readMessageLoop() {
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

    protected void handleMessage(String from, String message) {
        for(MessageListener listener : messageListeners){
            listener.onMessage(from, message);
        }
    }

    protected void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    protected void handleOnline(String[] tokens) throws IOException {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    protected boolean connect() throws IOException {
        try{
            socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            this.printWriter = new PrintWriter(new OutputStreamWriter(serverOut), true);
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

    public boolean create(String login, String password) throws IOException{
        return true;
    }

    public void joinRoom(String roomName) {
    }

    public String getLogin() {
        return login;
    }

    public void send(String s) throws IOException {
    }
}
