package user;

import java.io.*;
import java.util.HashSet;

public class UserContainer implements Serializable {
    private HashSet<User> users;

    public UserContainer() {
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

    public synchronized void saveToFile(String filePath) {
        try {
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
            out.close();
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized HashSet<User> LoadFromFile(String filePath) {
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);
            UserContainer userContainer = (UserContainer) in.readObject();
            setUsers(userContainer.getUsers());
            in.close();
            file.close();
        } catch (FileNotFoundException f) {
            setUsers(new HashSet<>());
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

}