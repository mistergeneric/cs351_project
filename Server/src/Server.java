import chat.ChatRoom;
import user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {

    private int serverPort;
    private List<ServerWorker> serverWorkers;
    private HashSet<ChatRoom> chatRooms;
    private HashSet<User> users;


    public Server(int serverPort) {
        this.serverPort = serverPort;
        serverWorkers = new ArrayList<>();
        this.chatRooms = new HashSet<>();
        this.users = new HashSet<>();
    }

    public HashSet<User> getUsers() {
        return users;
    }

    public void setUsers(HashSet<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
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
        if (findByChatRoomName(chatRoomName) != null) {
            ChatRoom chatRoom = findByChatRoomName(chatRoomName);
            chatRoom.getCurrentUsers().add(username);
        } else {
            HashSet<String> currentUsers = new HashSet<>();
            currentUsers.add(username);
            ChatRoom chatRoom = new ChatRoom(chatRoomName, currentUsers);
            chatRooms.add(chatRoom);
        }
    }

    public ChatRoom findByChatRoomName(String chatRoomName) {
        return chatRooms.stream().filter(chatRoom -> chatRoomName.equals(chatRoom.getChatRoomName())).findFirst().orElse(null);
    }

    public User findByUserName(String username) {
        User user = new User("test", "test");
        if(user.getUser(username, "users.txt") != null){
            return user.getUser(username, "users.txt");
        }
        return null;
        //return users.stream().filter(user -> username.equalsIgnoreCase(user.getLogin())).findFirst().orElse(null);
    }


    public String removeFromChatRoom(User user, String chatRoomName) {
        String responseMessage = "";
        if (findByChatRoomName(chatRoomName) != null) {
            ChatRoom chatRoom = findByChatRoomName(chatRoomName);
            chatRoom.getCurrentUsers().remove(user.getLogin());
            responseMessage += "You have been removed from the chat room\n";
            user.setCurrentChatRoom(null);
            if (chatRoom.getCurrentUsers().size() == 0) {
                chatRooms.remove(chatRoom);
                responseMessage += "Chat Room is empty and has been deleted\n";
            }
            return responseMessage;
        } else {
            responseMessage += "Chat room not found\n";
            return responseMessage;
        }
    }
}