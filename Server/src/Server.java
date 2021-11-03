import chat.ChatRoom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {

    private int serverPort;
    private List<ServerWorker> serverWorkers;
    private HashSet<ChatRoom> chatRooms;


    public Server(int serverPort) {
        this.serverPort = serverPort;
        serverWorkers = new ArrayList<>();
        this.chatRooms = new HashSet<>();
    }

    public List<ServerWorker> getServerWorkers() {
        return serverWorkers;
    }

    public HashSet<ChatRoom> getChatRooms() {
        return chatRooms;
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
                if (serverSocket != null) {
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

    public void removeWorker(ServerWorker serverWorker) {
        serverWorkers.remove(serverWorker);
    }

    public void addToChatRoom(String username, String chatRoomName) {
        if (findByName(chatRoomName) != null) {
            ChatRoom chatRoom = findByName(chatRoomName);
            chatRoom.getCurrentUsers().add(username);
        } else {
            HashSet<String> currentUsers = new HashSet<>();
            currentUsers.add(username);
            ChatRoom chatRoom = new ChatRoom(chatRoomName, currentUsers);
            chatRooms.add(chatRoom);
        }
    }

    public ChatRoom findByName(String chatRoomName) {
        return chatRooms.stream().filter(chatRoom -> chatRoomName.equals(chatRoom.getChatRoomName())).findFirst().orElse(null);
    }


}