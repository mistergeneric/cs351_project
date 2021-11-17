import org.junit.jupiter.api.Test;
import user.AdminUser;
import user.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AdminUserTest {

    @Test
    public void testAssignAdmin() throws IOException {
        User user = new User("Test", "Test");
        AdminUser adminUser = new AdminUser("admin","admin");

        AdminUser newAdmin = adminUser.assignAdmin(user);
        assertEquals(newAdmin.getLogin(), "Test");
    }

    @Test
    public void testDeleteUser() throws IOException {
        String filePath = "usersTest.txt";
        //Emptying the file before testing
        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        User user1 = new User("1", "1");
        user1.SaveToFile(filePath);
        User user2 = new User("2", "2");
        user2.SaveToFile(filePath);
        User user3 = new User("3", "3");
        user3.SaveToFile(filePath);
        User user4 = new User("4", "4");
        user4.SaveToFile(filePath);
        AdminUser admin = new AdminUser("admin", "admin");
        admin.SaveToFile(filePath);

        ArrayList<User> users = admin.LoadFromFile(filePath);

        assertEquals(5, users.size());

        admin.deleteUser(user2.getLogin(), filePath);

        users = admin.LoadFromFile(filePath);
        assertEquals(4, users.size());
    }
}