package chat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoom {
    private String chatRoomName;
    private HashSet<String> currentUsers;

    public ChatRoom(String chatRoomName, HashSet<String> currentUsers) {
        this.chatRoomName = chatRoomName;
        this.currentUsers = currentUsers;
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
}
