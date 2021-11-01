import chat.ChatRoom;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {
    private Socket clientSocket;
    private String login;
    private Server server;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.login = null;
        this.server = server;
    }

    public String getLogin() {
        return login;
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
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        outputStream.write("Welcome, please enter a command\n".getBytes());
        while ((line = reader.readLine()) != null) {
            String[] response = line.split(" ");
            if (response.length > 0) {
                String command = response[0];
                if ("quit".equalsIgnoreCase(line) || "logoff".equalsIgnoreCase(line)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(command)) {
                    handleLogin(outputStream, response);
                } else if ("msg".equalsIgnoreCase(command)) {
                    handleMessage(response);
                } else if ("join".equalsIgnoreCase(command)) {
                    handleJoin(response);
                } else {
                    String msg = "unknown " + command + "\n";
                    outputStream.write(msg.getBytes());
                }
            }

            String msg = "You typed: " + line + "\n";
            outputStream.write(msg.getBytes());
        }
        outputStream.write("Hello World\n".getBytes());
        clientSocket.close();
    }

    private void handleJoin(String[] response) throws IOException {
        if(response.length > 1 && response[1].charAt(0) == '#') {
            String topic = response[1];
            server.addToChatRoom(login, topic);
        }
        else {
            outputStream.write("Incorrectly formatted join \n".getBytes());
        }
    }

    private boolean isMemberOfGroup(String groupName) {
        if(server.findByName(groupName) != null) {
            boolean isPresentInChatRoom = server.findByName(groupName).getCurrentUsers().contains(login);
            return isPresentInChatRoom;
        }
        return false;
    }

    // "msg" "username" "message"
    private void handleMessage(String[] response) throws IOException {
        String sendTo = response[1];
        String msg = getMessageBody(response);

        List<ServerWorker> serverWorkers = server.getServerWorkers();

        if (sendTo.charAt(0) == '#') {
            if(isMemberOfGroup(sendTo)) {
                for(String user : server.findByName(sendTo).getCurrentUsers()) {
                    for (ServerWorker sw : serverWorkers) {
                        if (user.equalsIgnoreCase(sw.getLogin())) {
                            String outMsg = "msg for " + sendTo + " group from " + login + " " + getMessageBody(response) + "\n";
                            sw.send(outMsg);
                        }
                    }
                }
            }
        }
        else {
            for (ServerWorker sw : serverWorkers) {
                if (sendTo.equalsIgnoreCase(sw.getLogin())) {
                    String outMsg = "msg " + login + " " + msg + "\n";
                    sw.send(outMsg);
                }
            }
        }
    }

    private String getMessageBody(String[] response) {
        return String.join(" ", Arrays.copyOfRange(response, 2, response.length));
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> serverWorkers = server.getServerWorkers();
        String onlineMsg = "user offline: " + login + "\n";
        for (ServerWorker sw : serverWorkers) {
            if (sw.getLogin() != null && !sw.getLogin().equals(login)) {
                sw.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] response) throws IOException {
        if (response.length > 2) {
            String login = response[1];
            String password = response[2];
            if ((login.equalsIgnoreCase("guest") && password.equals("guest")) || (login.equalsIgnoreCase("andrew") && password.equals("andrew"))) {
                outputStream.write("Success\n".getBytes());
                this.login = login;
                System.out.println("User logged in successfully " + login);

                String onlineMsg = "user online: " + login + "\n";
                List<ServerWorker> serverWorkers = server.getServerWorkers();
                //send current user who is online
                for (ServerWorker sw : serverWorkers) {
                    //don't report on itself or not logged in user
                    if (sw.getLogin() != null && !sw.getLogin().equals(login)) {
                        String whoIsOnline = "online " + sw.getLogin() + "\n";
                        send(whoIsOnline);
                    }
                }
                //send other users the current status
                for (ServerWorker sw : serverWorkers) {
                    if (sw.getLogin() != null && !sw.getLogin().equals(login)) {
                        sw.send(onlineMsg);
                    }
                }
            } else {
                outputStream.write("Failed\n".getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }

}
