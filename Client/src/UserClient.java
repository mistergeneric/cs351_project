import java.util.HashSet;

public class UserClient {

    String id;
    String password;
    HashSet<UserClient> friends;
    HashSet<UserClient> likes;

    public UserClient(String id, String password){
        this.id = id;
        this.password = password;
        friends = new HashSet<>();
        likes = new HashSet<>();
    }


}
