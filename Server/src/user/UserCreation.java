package user;
//This is mostly a test class and allows to populate the first users
public class UserCreation {
    public static void main(String[] args) {
        String filePath = "users.txt";
        User nullUser = new User("access", "access");
        User camille = new User("camille", "camille");
        User jordan = new User("jordan", "jordan");
        User andrew = new User("andrew", "andrew");
        andrew.setDescription("hi");
        jordan.setDescription("hello");
        camille.SaveToFile(filePath);
        jordan.SaveToFile(filePath);
        andrew.SaveToFile(filePath);
        System.out.println(nullUser.isPasswordValid("camille", "Test", filePath));
        System.out.println(nullUser.isPasswordValid("camille", "camille", filePath));
        System.out.println(nullUser.isPasswordValid("jordan", "hi", filePath));
        System.out.println(nullUser.isPasswordValid("jordan", "jordan", filePath));
        System.out.println(nullUser.isPasswordValid("andrew", "hello", filePath));
        System.out.println(nullUser.isPasswordValid("andrew", "andrew", filePath));
    }
}
