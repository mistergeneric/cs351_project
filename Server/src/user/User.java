package user;

import client.ChatClient;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class User implements Serializable {
    private String login;
    private String password;
    private String description;
    private HashSet<String> friends;
    private HashSet<String> likes;
    private String currentChatRoom;
    protected ChatClient chatClient;
    private static final long serialVersionUID = 6529685098267757690L;
    boolean isAdmin;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.friends = new HashSet<>();
        this.likes = new HashSet<>();
        this.currentChatRoom = null;
        isAdmin = false;
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

    public void setChatClient(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public void SaveToFile(String filePath)
    {
        ArrayList<User> users = new ArrayList<>();
        if(LoadFromFile(filePath).size() != 0){
            users = LoadFromFile(filePath);
        }
        users.add(this);
        try{
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(users);
            out.close();
            file.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<User> LoadFromFile(String filePath){
        ArrayList<User> users = new ArrayList<>();
        Boolean keepReading = true;
        try{
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);
            while(keepReading){
                users = (ArrayList<User>) in.readObject();
            }
            in.close();
            file.close();
        } catch (EOFException e){
            keepReading = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUser(String login, String filePath){
        ArrayList<User> users = LoadFromFile(filePath);
        for(User user: users){
            if(user.getLogin().equalsIgnoreCase(login)){
                return user;
            }
        }
        return null;
    }

    public boolean isPasswordValid(String login, String password, String filePath){
        ArrayList<User> users = LoadFromFile(filePath);
        for(User user: users){
            if(user.getLogin().equalsIgnoreCase(login)){
                if(user.getPassword().equals(password)){
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean getIsAdmin(){
        return isAdmin;
    }

}
