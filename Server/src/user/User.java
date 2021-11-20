package user;

import java.io.*;
import java.util.HashSet;

public class User implements Serializable {
    private String login;
    private String password;
    private String description;
    private HashSet<String> friends;
    private HashSet<String> likes;
    boolean isAdmin;
    boolean showLikes;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.friends = new HashSet<>();
        this.likes = new HashSet<>();
        isAdmin = false;
        showLikes = true;
    }

    public void setIsAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
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

    public boolean addFriend(String friend) {
        return friends.add(friend.toLowerCase());
    }

    public boolean addLike(String likedBy) {
        return likes.add(likedBy.toLowerCase());
    }

    public boolean getIsAdmin(){
        return isAdmin;
    }

    public boolean showLikes() {
        return showLikes;
    }

    public void setShowLikes(boolean showLikes) {
        this.showLikes = showLikes;
    }

}
