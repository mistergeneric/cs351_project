import client.ChatClient;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestRooms {

    @Test
    public void testRooms() throws InterruptedException, IOException {
        Server server = new Server(8819);
        if (new File("userStore.txt").delete()) {
            server = new Server(8819);
        }
        new File("userStore.txt").delete();
        server.start();
        ExecutorService executor = Executors.newFixedThreadPool(101);
        List<ChatClient> clients = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ChatClient client = new ChatClient("localhost",8819);
            client.connect();
            client.send("register " + i + " pw\n");
            client.setLogin(String.valueOf(i));
            clients.add(client);
        }
        int x = 0;
        while (server.getServerWorkers().size() < 99) {
            // wait for all registrations and logins.
        }
        for (ChatClient client : clients) {
            Thread t = new Thread(client.getLogin()) {
                @Override
                public void run() {

                    try {
                        client.send("join #testRoom\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            };
            executor.submit(t);
        }
        long now = System.nanoTime();
        while (System.nanoTime() < now + 1000000) {

        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        ArrayList<ChatRoom> rooms = new ArrayList<>(server.getChatRooms());
        assert rooms.size() == 1;
        assert rooms.get(0).getUsers().size() == 100;
    }
}
