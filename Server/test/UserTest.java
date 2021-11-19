import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testSetAdmin(){
        User user = new User("test", "test");
        assertFalse(user.getIsAdmin());
        user.setIsAdmin(true);
        assertTrue(user.getIsAdmin());
    }

    @Test
    public void testGetPassword(){
        User user = new User("test", "test");
        String expected = "test";
        assertEquals(expected, user.getPassword());
    }

    @Test
    public void test_friendList(){
        User user = new User("test", "test");
        assertEquals(0, user.getFriends().size());
        user.addFriend("Friend1");
        user.addFriend("Friend2");
        assertEquals(2, user.getFriends().size());
    }

    @Test
    public void test_likes(){
        User user = new User("test", "test");
        assertEquals(0, user.getLikes().size());
        user.addLike("User1");
        user.addLike("User2");
        assertEquals(2, user.getLikes().size());
    }


}