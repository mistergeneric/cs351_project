import org.junit.jupiter.api.Test;
import user.User;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void getUserTest() throws FileNotFoundException {
        String filePath = "usersTest.txt";
        //Emptying the file before testing
        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        User user1 = new User("1", "1");
        user1.SaveToFile(filePath);
        User user2 = new User("This", "2");
        user2.SaveToFile(filePath);
        User user3 = new User("3", "3");
        user3.SaveToFile(filePath);
        User user4 = new User("4", "4");
        user4.SaveToFile(filePath);

        String expectedName = "This";
        String actualName = user1.getUser("This", filePath).getLogin();
        String expectedPw = "2";
        String actualPw = user1.getUser("This", filePath).getPassword();
        assertEquals(expectedName, actualName);
        assertEquals(expectedPw, actualPw);
    }

    @Test
    public void isPasswordValidTest() throws FileNotFoundException {
        String filePath = "usersTest.txt";
        //Emptying the file before testing
        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        User user1 = new User("1", "1");
        user1.SaveToFile(filePath);
        User user2 = new User("test", "test");
        user2.SaveToFile(filePath);

        boolean actualTrue = user1.isPasswordValid("test", "test", filePath);
        boolean actualFalse = user1.isPasswordValid("test", "invalid", filePath);
        assertTrue(actualTrue);
        assertFalse(actualFalse);
    }

}