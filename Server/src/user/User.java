package user;

import java.util.HashSet;

public class User {
    private String login;
    private String password;
    private String description;
    private HashSet<String> friends;
    private HashSet<String> likes;
    private String currentChatRoom;

    public User(String login, String password, String description) {
        this.login = login;
        this.password = password;
        this.description = description;
        this.friends = new HashSet<>();
        this.likes = new HashSet<>();
        this.currentChatRoom = null;
    }

    public HashSet<String> getFriends() {
        return friends;
    }

    public void setFriends(HashSet<String> friends) {
        this.friends = friends;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getLikes() {
        return likes;
    }

    public void setLikes(HashSet<String> likes) {
        this.likes = likes;
    }

    public String getCurrentChatRoom() {
        return currentChatRoom;
    }

    public void setCurrentChatRoom(String currentChatRoom) {
        this.currentChatRoom = currentChatRoom;
    }

    public boolean addFriend(String friend) {
        return friends.add(friend.toLowerCase());
    }

    public boolean addLike(String likedBy) {
        return likes.add(likedBy.toLowerCase());
    }

}