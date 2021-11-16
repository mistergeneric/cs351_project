import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatRoom {
    private String chatRoomName;
    private HashSet<String> currentUsers;
    private HashSet<ServerWorker> users;

    public ChatRoom(String chatRoomName, HashSet<String> currentUsers) {
        this.chatRoomName = chatRoomName;
        this.currentUsers = currentUsers;
        users = new HashSet<>();
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public Set<String> getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(HashSet<String> currentUsers) {
        this.currentUsers = currentUsers;
    }

    public void addUser(ServerWorker worker) {
        users.add(worker);
    }

    public void removeUser(ServerWorker worker) {
        users.remove(worker);
    }

    public HashSet<ServerWorker> getUsers() {
        return users;
    }
}
