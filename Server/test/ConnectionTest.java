import client.ChatClient;
import org.junit.Test;
import user.User;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionTest {


    @Test
    public void testManyLikes() throws InterruptedException, IOException {
        Server server = new Server(8818);
        if (new File("userStore.txt").delete()) {
            server = new Server(8818);
        }
        new File("userStore.txt").delete();
        server.start();
        ExecutorService executor = Executors.newFixedThreadPool(101);
        List<ChatClient> clients = new ArrayList<>();
        ChatClient goodGuy = new ChatClient("localhost",8818);
        goodGuy.connect();
        goodGuy.send("register goodGuy pw\n");
        Thread.sleep(2000); // give him time to exist before the threads start registering and liking.
        for (int i = 0; i < 100; i++) {
            ChatClient client = new ChatClient("localhost",8818);
            client.connect();
            client.send("register " + i + " pw\n");
            client.setLogin(String.valueOf(i));
            clients.add(client);
        }
        while (server.getServerWorkers().size() < 100) {
            // wait for all registrations and logins.
        }
        int clientSent[] = new int[clients.size()] ;
        int index = 0;
        for (ChatClient client : clients) {
            Thread t = new Thread(client.getLogin()) {
                @Override
                public void run() {

                    try {
                        client.send("like goodGuy\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            };
            executor.submit(t);
            clientSent[index] = 1;
            index++;
        }

        while (server.findByUserName("goodGuy").getLikes().size() < 100) {
            /* unfortunately I need to wait until the server has processed all the responses, unsure how to
            check this, however this test has stopped Concurrent Modification when 1000 users login, and ensures
            the correct number of likes are processed at the same time, as the errors were taken care of as they were
            found*/
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        User u = server.findByUserName("goodGuy");
        HashSet<String> likes = u.getLikes();
        assertEquals(likes.size(),clients.size());
    }


}