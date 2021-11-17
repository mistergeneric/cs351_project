package user;

import java.util.ArrayList;

//This is mostly a test class and allows to populate the first users
public class UserCreation {
    public static void main(String[] args) {
        String filePath = "users.txt";
        User nullUser = new User("access", "access");
        AdminUser admin = new AdminUser("admin", "admin");
        //nullUser.SaveToFile(filePath);
        //admin.SaveToFile(filePath);
        ArrayList<User> users = new ArrayList<>();
        users = nullUser.LoadFromFile(filePath);
        for(User user: users){
            System.out.println(user.getLogin());
        }
    }
}
