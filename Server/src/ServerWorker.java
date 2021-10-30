import java.io.*;
import java.net.Socket;
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
                } else {
                    String msg = "unknown " + command + "\n";
                    outputStream.write(msg.getBytes());
                }
            }

            String msg = "You typed: " + line;
            outputStream.write(msg.getBytes());
        }
        outputStream.write("Hello World\n".getBytes());
        clientSocket.close();
    }

    private void handleLogoff() throws IOException {
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
