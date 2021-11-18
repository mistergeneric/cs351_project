package client;

import javax.swing.*;
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
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private PrintWriter printWriter;
    private String login;
    private String responseText;

    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        Scanner scanner = new Scanner(System.in);

        if(!client.connect()){
            System.err.println("Connection failed");
        }else{
            System.out.println("Connection successful");

            client.addMessageListener(new MessageListener() {
                @Override
                public void onMessage(String from, String message) {
                    System.out.println(from + " " + message );
                }
            });

            client.userInput();
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
        String cmd = "msg " +  message + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
        //user.SaveToFile(filePath);
    }

    protected boolean login(String id, String password) throws IOException {
        String cmd = "login " + id + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        responseText = bufferedIn.readLine();

        if ("Success".equalsIgnoreCase(responseText)){
            startMessageReader();
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
                    if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = line.split(" ");
                        String message = String.join(" ", Arrays.copyOfRange(tokensMsg, 2, tokensMsg.length));
                        handleMessage(tokensMsg[1], message);
                    }else if (line.contains("would like to add you as a friend!")) {
                        int response = JOptionPane.showConfirmDialog(null,line + "\nAccept?","Friends",JOptionPane.YES_NO_OPTION);
                        handleFriendRequest(response,cmd);
                    } else if ("Chatrooms".equalsIgnoreCase(cmd)) {
                        handleChatrooms(tokens);
                    } else if ("users".equalsIgnoreCase(cmd)) {
                        handleUserList(tokens);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,line);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try{
                JOptionPane.showMessageDialog(null,e.getMessage());
                socket.close();
                System.exit(0);
            }catch(IOException ex){
                ex.printStackTrace();
            }

        }
    }

    private void handleUserList(String[] tokens) {
        String users = String.join("\n",tokens);
        JOptionPane.showMessageDialog(null,users);
    }

    private void handleChatrooms(String[] tokens) {
        String chatrooms = String.join("\n",tokens);
        JOptionPane.showMessageDialog(null,chatrooms);
    }

    private void handleFriendRequest(int response, String user) throws IOException {
        String msg;
        if (response == JOptionPane.YES_OPTION) {
            msg = "accept " +  user + "\n";
        } else {
            msg = "reject " +  user + "\n";
        }
        send(msg);
    }

    protected void handleMessage(String from, String message) {
        for(MessageListener listener : messageListeners){
            listener.onMessage(from, message);
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
            JOptionPane.showMessageDialog(null,"Error connecting to server", "Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return false;
    }

    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }

    public boolean create(String login, String password) throws IOException{
        String cmd = "register " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        responseText = bufferedIn.readLine();

        if ("Success".equalsIgnoreCase(responseText)){
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public void joinRoom(String roomName) throws IOException {
        String cmd = "join #" + roomName + "\n";
        send(cmd);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void send(String s) throws IOException {
        serverOut.write(s.getBytes());
    }

    public String getResponseText() {
        return responseText;
    }
}
