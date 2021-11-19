import org.junit.jupiter.api.Test;
import user.User;
import user.UserContainer;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserContainerTest {

    private UserContainer userContainer = new UserContainer();
    private String filePath = "testUserContainer.txt";

    @Test
    public void test_loadFromFile_SaveToFile() throws FileNotFoundException {
        //Emptying the file before testing
        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        assertEquals(0, userContainer.LoadFromFile(filePath).size());
        userContainer.addUser(new User("user1", "user1"));
        userContainer.addUser(new User("user2", "user2"));
        userContainer.saveToFile(filePath);
        assertEquals(2, userContainer.LoadFromFile(filePath).size());
    }

    //https://stackoverflow.com/questions/26967036/how-to-unit-test-a-synchronized-method
    @Test
    public void test_multiple_writeToFile() throws InterruptedException, FileNotFoundException {
        //Emptying the file before testing
        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        int numThreads = 10;
        int numIterations = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Random random = new Random();
        UserContainer userContainer = new UserContainer();
        int randomInt = random.nextInt(100000);
        int finalRandomInt = randomInt;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numIterations; i++) {
                    String userName = "user" + (i * finalRandomInt - i);
                    userContainer.addUser(new User(userName, "pw"));
                    userContainer.saveToFile(filePath);
                }
            }
        });

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        int finalNumberOfUsers = userContainer.LoadFromFile(filePath).size();
        assertEquals((numIterations), finalNumberOfUsers);
    }
}
