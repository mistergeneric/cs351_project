import java.net.DatagramSocket;
import java.util.HashSet;

public class UserClient {

    String id;
    String password;
    String description;
    HashSet<UserClient> friends;
    HashSet<UserClient> likedBy;
    ChatClient chatClient;
    DataHandling dataHandling;

    public UserClient(String id, String password){
        this.id = id;
        this.password = password;
        friends = new HashSet<>();
        likedBy = new HashSet<>();
        dataHandling = new DataHandling();
    }

    //Check whether the password is correct
    boolean checkPassword(){
        if(userExists()){
            //Check if the password is correct
            return true;
        }
        dataHandling.createUser(this);
        return true;
    }

    boolean userExists(){
        if(dataHandling.loadUser(this) != null){
            return true;
        }
        return false;
    }

    public void setDescription(String description){
        this.description = description;
        dataHandling.updateDescription(this, description);
    }

    public String getDescription(){
        return description;
    }

    public void setChatClient(ChatClient chatClient){
        this.chatClient = chatClient;
    }
}
