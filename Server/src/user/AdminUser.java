package user;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class AdminUser extends User implements Serializable {

    Socket socket;
    public AdminUser(String login, String password) {
        super(login, password);
        isAdmin = true;
    }

    public void editDescription(User user, String description){
        user.setDescription(description);
    }

    public void kickUser(User user) throws IOException {
        user.chatClient.logoff();
    }

    public void broadcastMessage(String message, String serverName, int serverPort) throws IOException {
        socket = new Socket(serverName, serverPort);

    }

    public AdminUser assignAdmin(User user) throws IOException {
        String filePath = "users.txt";
        //Create a new admin with the name and password of the user
        AdminUser newAdmin = new AdminUser(user.getLogin(), user.getPassword());
        //If they have a description, set the admin's description to the old description
        if(user.getDescription() != null){
            newAdmin.setDescription(user.getDescription());
        }
        //Delete the user from the user log
        deleteUser(user.getLogin(), filePath);
        //And add the admin into the user log
        SaveToFile(filePath);
        return newAdmin;
    }

    public void deleteUser(String userToDeleteLogin, String filePath) throws IOException {
        ArrayList<User> users = LoadFromFile(filePath);
        User userToRemove = null;
        for(User userInList : users){
            if(userInList.getLogin().equals(userToDeleteLogin)){
                userToRemove = userInList;
            }
        }
        if(userToRemove != null){
            users.remove(userToRemove);
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
    }
}
