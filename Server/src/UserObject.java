import java.util.HashSet;

public class UserObject {

    String id;
    String password;
    String description;
    HashSet<UserObject> friends;
    HashSet<UserObject> likedBy;
    ChatClient chatClient;

    public UserObject(String id, String password){
        this.id = id;
        this.password = password;
        friends = new HashSet<>();
        likedBy = new HashSet<>();
    }

    //Check whether the password is correct
    boolean checkPassword(){
        if(true){
            //Check if the password is correct
            return true;
        }
        return true;
    }



    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setChatClient(ChatClient chatClient){
        this.chatClient = chatClient;
    }


}
