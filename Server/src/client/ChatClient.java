package client;

import java.io.IOException;

/**
 * Dummy client so GUI can operate
 */
public class ChatClient {

    private String login;

    public ChatClient(String localhost, int i) {

    }

    public void connect() {
    }

    public boolean login(String login, String password) throws IOException {
        return true;
    }

    public boolean create(String login, String password) throws IOException{
        return true;
    }

    public void logoff() throws IOException{
    }

    public void msg(String login, String msg) throws IOException {

    }

    public void addMessageListener(MessageListener listener) {

    }

    public void joinRoom(String roomName) {
    }

    public void send(String s) throws IOException {
    }

    public String getLogin() {
        return login;
    }
}
