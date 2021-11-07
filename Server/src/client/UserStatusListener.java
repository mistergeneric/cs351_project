package client;

import java.io.IOException;

public interface UserStatusListener {

    public void online(String login) throws IOException;
    public void offline(String login);
}
