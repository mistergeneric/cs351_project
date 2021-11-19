import user.User;
import user.UserContainer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {

    private final String USER_STORE = "userStore.txt";

    private int serverPort;
    private List<ServerWorker> serverWorkers;
    private HashSet<ChatRoom> chatRooms;
    private HashSet<User> users;
    private final UserContainer userContainer;
    private final Object lock = new Object();


    public Server(int serverPort) {
        this.serverPort = serverPort;
        serverWorkers = new ArrayList<>();
        this.chatRooms = new HashSet<>();
        this.users = new HashSet<>();
        userContainer = new UserContainer();
    }

    public HashSet<User> getUsers() {
        return userContainer.getUsers();
    }

    public void setUsers(HashSet<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        // stop other modifications while writing.
        synchronized (lock) {
            userContainer.addUser(user);
            userContainer.saveToFile(USER_STORE);
        }
    }

    public void removeUser(User user) {
        userContainer.removeUser(user);
        userContainer.saveToFile(USER_STORE);
    }

    public void updateStore() {
        synchronized (lock) {
            userContainer.saveToFile(USER_STORE);
        }
    }

    public void loadStore() {
        userContainer.LoadFromFile(USER_STORE);
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
        loadStore();
        User admin = new User("admin","admin");
        admin.setIsAdmin(true);
        addUser(admin);
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
        // avoid reading whilst it is being updated
        synchronized (lock) {
            return userContainer.getUsers().stream().filter(user -> username.equalsIgnoreCase(user.getLogin())).findFirst().orElse(null);
        }

    }


    public String removeFromChatRoom(User user, String chatRoomName) {
        String responseMessage = "";
        if (findByChatRoomName(chatRoomName) != null) {
            ChatRoom chatRoom = findByChatRoomName(chatRoomName);
            chatRoom.getCurrentUsers().remove(user.getLogin());
            responseMessage += "You have been removed from the chat room\n";
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