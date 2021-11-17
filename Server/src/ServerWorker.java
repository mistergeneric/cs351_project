import user.AdminUser;
import user.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private User user;
    private final Server server;
    private OutputStream outputStream;
    private HashSet<String> friendRequests;
    private ChatRoom currentChatroom;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.user = null;
        this.server = server;
        this.friendRequests = new HashSet<>();
    }

    public String getLogin() {
        if (user != null) {
            return user.getLogin();
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            try {
                server.removeWorker(this);
                handleLogoff();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.err.println("Client disconnect");
            //e.printStackTrace();
        }
    }


    //this is the 'meat and potatoes' so this is where we get the message or whatever else and then we'll process it
    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Server received: " + line);
            String[] response = line.split(" ");
            if (response.length > 0) {
                String command = response[0];
                if ("quit".equalsIgnoreCase(line) || "logoff".equalsIgnoreCase(line)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(command)) {
                    handleLogin(outputStream, response);
                } else if ("msg".equalsIgnoreCase(command)) {
                    if (user != null) {
                        handleMessage(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("join".equalsIgnoreCase(command)) {
                    if (user != null) {
                        handleJoin(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("leave".equalsIgnoreCase(command)) {
                    if (user != null) {
                        handleLeave(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("register".equalsIgnoreCase(command)) {
                    handleRegister(response);
                } else if ("friend".equalsIgnoreCase(command)) {
                    if (user != null) {
                        handleFriendRequest(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("requests".equalsIgnoreCase(command)) {
                    if (user != null) {
                        friendRequestMenu();
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("accept".equalsIgnoreCase(command)) {
                    if (user != null) {
                        acceptFriendRequest(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("reject".equalsIgnoreCase(command)) {
                    if (user != null) {
                        rejectFriendRequest(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("details".equalsIgnoreCase(command)) {
                    if (user != null) {
                        getUserDetails(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("update".equalsIgnoreCase(command)) {
                    if (user != null) {
                        updateDetails(line.split(" ",2));
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("like".equalsIgnoreCase(command)) {
                    if (user != null) {
                        likeUser(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("friendList".equalsIgnoreCase(command)) {
                    if (user != null) {
                        friendList(response);
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if ("friendsInChat".equalsIgnoreCase(command)) {
                    if (user != null) {
                        friendsInChatRooms();
                    } else {
                        outputStream.write("You must login first\n".getBytes());
                    }
                } else if("broadcast".equalsIgnoreCase(command)){
                    if(user != null){
                        broadcast(response);
                    }
                } else if ("showrooms".equalsIgnoreCase(command)) {
                    if (user != null) {
                        handleRoomList();
                    }
                } else if ("viewusers".equalsIgnoreCase(command)) {
                    handleUsersInRoom(response);
                } else if("editDescription".equalsIgnoreCase(command)){
                    handleEditUserDescription(response);
                } else if("delete".equalsIgnoreCase(command)){
                    handleDeleteUser(response);
                } else if("kick".equalsIgnoreCase(command)){
                    handleKickUser(response);
                }
                else {
                    String msg = "unknown " + command + "\n";
                    outputStream.write(msg.getBytes());
                }
            }

            //String msg = "You typed: " + line + "\n";
            //outputStream.write(msg.getBytes());
        }
    }

    private void handleUsersInRoom(String[] response) throws IOException {
        ChatRoom room = server.findByChatRoomName('#' + response[1]);
        if (room == null) {
            send("No such room exists\n");
        } else {
            StringBuilder msg = new StringBuilder("users ");
            for (ServerWorker worker : room.getUsers()) {
                msg.append(worker.getLogin()).append(" ");
            }
            send(msg + "\n");
        }
    }

    private void handleRoomList() throws IOException {
        StringBuilder msg = new StringBuilder("Chatrooms ");
        if (!server.getChatRooms().isEmpty()) {
            for (ChatRoom room : server.getChatRooms()) {
                msg.append(room.getChatRoomName()).append(" ");
            }
            send(msg + "\n");
        } else {
            send("No chatroom created yet\n");
        }
    }

    //Is response here necessary?
    private void friendList(String[] response) throws IOException {
        if (user.getFriends().size() > 0) {
            String friendsList = "Your friends:\n";
            for (String friend : user.getFriends()) {
                friendsList += friend + " | ";
            }
            friendsList += "\n";
            outputStream.write(friendsList.getBytes());
        } else {
            outputStream.write("You have no friends\n".getBytes());
        }
    }

    public void friendsInChatRooms() throws IOException {
        List<ServerWorker> serverWorkers = server.getServerWorkers();
        String message = "";
        for (ServerWorker sw : serverWorkers) {
            if (user.getFriends().contains(sw.getUser().getLogin().toLowerCase())) {
                if (sw.getUser().getCurrentChatRoom() != null) {
                    message = sw.getLogin() + " is in chat room " + sw.getUser().getCurrentChatRoom() + "\n";
                }
                else if (isLoggedIn(sw.getLogin())) {
                    message = sw.getLogin() + " not in a room " + "\n";
                }
                else {
                    message = sw.getLogin() + " is offline " + "\n";
                }
            }
            send(message);
        }
        if (message.equals("")) {
            outputStream.write("No friends online\n".getBytes());
        }
    }

    private void likeUser(String[] response) throws IOException {
        if (response.length > 1) {
            boolean foundUser = false;
            String userForDetails = response[1];
            List<ServerWorker> serverWorkers = server.getServerWorkers();
            for (ServerWorker sw : serverWorkers) {
                if (userForDetails.equalsIgnoreCase(sw.getLogin())) {
                    boolean addedLike = sw.getUser().addLike(user.getLogin().toLowerCase());
                    if (addedLike) {
                        outputStream.write("You've liked this user! \n".getBytes());
                        server.updateStore();
                    } else {
                        outputStream.write("You've already liked this user!\n".getBytes());
                    }
                    foundUser = true;
                }
            }
            if (!foundUser) {
                outputStream.write("User details not found\n".getBytes());
            }
        } else {
            outputStream.write("incorrectly formatted command\n".getBytes());
        }

    }

    private void updateDetails(String[] response) throws IOException {
        if (response.length > 1) {
            String detailsToUpdate = response[1];
            String details = "current user bio: " + user.getDescription() + "\n";
            outputStream.write(details.getBytes());
            details = "new user bio: " + detailsToUpdate + "\n";
            outputStream.write(details.getBytes());
            user.setDescription(detailsToUpdate);
            outputStream.write("successfully updated".getBytes());
            server.updateStore();
        }
    }

    private void getUserDetails(String[] response) throws IOException {
        if (response.length > 1) {
            boolean foundUser = false;
            String userForDetails = response[1];
            List<ServerWorker> serverWorkers = server.getServerWorkers();
            for (ServerWorker sw : serverWorkers) {
                if (userForDetails.equalsIgnoreCase(sw.getLogin())) {
                    String details = "requested user bio: " + sw.getUser().getDescription() + "\n";
                    details += "number of likes: " + sw.getUser().getLikes().size() + "\n";
                    outputStream.write(details.getBytes());
                    foundUser = true;
                }
            }
            if (!foundUser) {
                outputStream.write("User details not found\n".getBytes());
            }
        } else {
            outputStream.write("incorrectly formatted command\n".getBytes());
        }
    }

    private void rejectFriendRequest(String[] response) throws IOException {
        if (friendRequests.size() > 0) {
            String userToRemove = response[1];
            if (friendRequests.contains(userToRemove.toLowerCase())) {
                List<ServerWorker> serverWorkers = server.getServerWorkers();
                for (ServerWorker sw : serverWorkers) {
                    if (userToRemove.equalsIgnoreCase(sw.getLogin())) {
                        removeRequest(userToRemove.toLowerCase());
                        outputStream.write("Friend removed\n".getBytes());
                        sw.send(user.getLogin() + " has rejected your friend request\n");
                        server.updateStore();
                    }
                }
            } else {
                outputStream.write("User not in your friend requests\n".getBytes());
            }
        } else {
            outputStream.write("You have no friend requests\n".getBytes());
        }
    }

    private boolean removeRequest(String login) {
        return friendRequests.remove(login.toLowerCase());
    }

    public boolean addRequest(String name) {
        return friendRequests.add(name.toLowerCase());
    }

    private void friendRequestMenu() throws IOException {
        this.outputStream = clientSocket.getOutputStream();
        if (friendRequests.size() > 0) {
            outputStream.write("Here are your friend requests\n".getBytes());
            for (String request : friendRequests) {
                String message = request + " wants to be your friend\n";
                outputStream.write(message.getBytes());
            }
        } else {
            outputStream.write("You have no friend requests\n".getBytes());
        }
    }

    private void acceptFriendRequest(String[] response) throws IOException {
        if (friendRequests.size() > 0) {
            String userToAdd = response[1];
            if (friendRequests.contains(userToAdd.toLowerCase())) {
                List<ServerWorker> serverWorkers = server.getServerWorkers();
                for (ServerWorker sw : serverWorkers) {
                    if (userToAdd.equalsIgnoreCase(sw.getLogin())) {
                        user.addFriend(userToAdd.toLowerCase());
                        sw.getUser().addFriend(user.getLogin());
                        outputStream.write("Friend added\n".getBytes());
                        sw.send(user.getLogin() + " has accepted your friend request\n");
                        server.updateStore();
                    }
                }
            } else {
                outputStream.write("User not in your friend requests\n".getBytes());
            }
        } else {
            outputStream.write("You have no friend requests\n".getBytes());
        }

    }

    private User getUser() {
        return user;
    }

    private void handleFriendRequest(String[] response) throws IOException {
        if (response.length > 1) {
            String friendToAdd = response[1];
            if (user.getFriends().contains(friendToAdd.toLowerCase())) {
                outputStream.write("Friend already added\n".getBytes());
            } else {
                if (friendToAdd.equalsIgnoreCase(user.getLogin())) {
                    outputStream.write("You can't send yourself a friend request\n".getBytes());
                } else {
                    sendFriendRequest(user.getLogin(), friendToAdd);
                }
            }

        } else {
            outputStream.write("incorrectly formatted command\n".getBytes());
        }
    }

    private void sendFriendRequest(String login, String friendToAdd) throws IOException {
        List<ServerWorker> serverWorkers = server.getServerWorkers();
        for (ServerWorker sw : serverWorkers) {
            if (friendToAdd.equalsIgnoreCase(sw.getLogin())) {
                if(user.getCurrentChatRoom() == null) {
                    outputStream.write("You need to be in a chat room to send \n".getBytes());
                }
                else if(sw.getUser().getCurrentChatRoom() == null) {
                    outputStream.write("The other user needs to be in a chat room to send \n".getBytes());
                }
                else if (user.getCurrentChatRoom().equalsIgnoreCase(sw.getUser().getCurrentChatRoom())) {
                    String outMsg = login + " would like to add you as a friend!\n";
                    sw.send(outMsg);
                    boolean success = sw.addRequest(user.getLogin());
                    if (success) {
                        outputStream.write("successfully sent friend request \n".getBytes());
                    } else {
                        outputStream.write("friend request was already sent\n".getBytes());
                    }
                } else {
                    outputStream.write("You can only send friend requests to those in the current chat room \n".getBytes());
                }
            }
        }
    }


    private void handleRegister(String[] response) throws IOException {
        if (response.length > 2) {
            String login = response[1];
            String password = response[2];
            if (server.findByUserName(login) == null) {
                User user = new User(login, password);
                //outputStream.write("Success\n".getBytes());
                System.out.println("User registered successfully " + login);
                server.addUser(user);
                server.updateStore();
                handleLogin(outputStream,response);
            } else {
                outputStream.write("User already exists, please login \n".getBytes());
            }
        } else {
            outputStream.write("incorrectly formatted command\n".getBytes());
        }
    }

    private void handleLeave(String[] response) throws IOException {
        if (currentChatroom == null) {
            outputStream.write("You are not in a chatroom\n".getBytes());
        } else {
            String message = server.removeFromChatRoom(user, currentChatroom.getChatRoomName());
            currentChatroom.removeUser(this);
            currentChatroom = null;
            outputStream.write(message.getBytes());
        }
    }

    private void handleJoin(String[] response) throws IOException {
        if (response.length > 1 && response[1].charAt(0) == '#') {
            if (user.getCurrentChatRoom() == null) {
                String topic = response[1];
                server.addToChatRoom(user.getLogin(), topic);
                user.setCurrentChatRoom(topic);
                this.currentChatroom = server.findByChatRoomName(topic);
                currentChatroom.addUser(this);
                outputStream.write("You have joined the chat \n".getBytes());

            } else {
                outputStream.write("You have to leave a chat room before joining a new one \n".getBytes());
            }
        } else {
            outputStream.write("Incorrectly formatted join \n".getBytes());
        }
    }

    private boolean isMemberOfGroup(String groupName) {
        if (server.findByChatRoomName(groupName) != null) {
            return server.findByChatRoomName(groupName).getCurrentUsers().contains(user.getLogin());
        }
        return false;
    }

    // "msg" "username" "message"
    private void handleMessage(String[] response) throws IOException {
        String sendTo = response[1];
        String msg = getMessageBody(response);
        for (ServerWorker worker : currentChatroom.getUsers()) {
            if (!worker.getLogin().equals(this.getLogin())) {
                worker.send("msg " + this.getLogin() + " " + msg + "\n");
            }
        }
    }

    private String getMessageBody(String[] response) {
        return String.join(" ", Arrays.copyOfRange(response, 1, response.length));
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        currentChatroom.removeUser(this);
        server.removeFromChatRoom(this.user,currentChatroom.getChatRoomName());
        currentChatroom=null;
        user.setCurrentChatRoom(null);
        server.updateStore();
        List<ServerWorker> serverWorkers = server.getServerWorkers();
        String onlineMsg = "user offline: " + user.getLogin() + "\n";
        for (ServerWorker sw : serverWorkers) {
            if (sw.getLogin() != null && (!(sw.getLogin().equals(user.getLogin()))) && user.getFriends().contains(sw.getLogin())) {
                sw.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] response) throws IOException {
        if (response.length > 2) {
            String login = response[1];
            String password = response[2];
            if (server.findByUserName(login) != null) {

                    User user = server.findByUserName(login);

                if (isLoggedIn(login)) {
                    outputStream.write("I'm sorry, user is already logged in\n".getBytes());
                } else {
                    if (server.findByUserName(login).getPassword().equals(password)) {
                        outputStream.write("Success\n".getBytes());
                        this.user = user;
                        user.setCurrentChatRoom(null);
                        System.out.println("User logged in successfully " + login);
                        List<ServerWorker> serverWorkers = server.getServerWorkers();
                        String onlineMsg = "user online: " + login + "\n";
                        //send current user who is online
                        for (ServerWorker sw : serverWorkers) {
                            //don't report on itself or not logged in user
                            if (sw.getLogin() != null && (!(sw.getLogin().equals(login))) && sw.getUser().getFriends().contains(login)) {
                                System.out.println(sw.getLogin());
                                System.out.println(login);
                                System.out.println(sw.getUser().getFriends().contains(login));
                                String whoIsOnline = "online " + sw.getLogin() + "\n";
                                send(whoIsOnline);
                            }
                        }
                        //send other users the current status
                        for (ServerWorker sw : serverWorkers) {
                            if (sw.getLogin() != null && (!(sw.getLogin().equals(login))) && sw.getUser().getFriends().contains(login)) {
                                sw.send(onlineMsg);
                            }
                        }
                    } else {
                        outputStream.write("Password incorrect, please try again\n".getBytes());
                    }
                }
            } else {
                outputStream.write("User not found, please register\n".getBytes());
            }
        } else {
            outputStream.write("incorrectly formatted command\n".getBytes());
        }
    }

    private void send(String msg) throws IOException {
        if (user != null) {
            outputStream.write(msg.getBytes());
        }
    }

    private boolean isLoggedIn(String login) {
        boolean isLoggedIn = false;
        List<ServerWorker> serverWorkers = server.getServerWorkers();
        for (ServerWorker sw : serverWorkers) {
            if (sw.getUser() != null) {
                if (sw.getUser().getLogin().equalsIgnoreCase(login)) {
                    isLoggedIn = true;
                }
            }
        }
        return isLoggedIn;
    }


    private void broadcast(String[] response) throws IOException {
        String message = String.join(" ", Arrays.copyOfRange(response, 1, response.length));
        if(user.getIsAdmin()){
            //Loop through all the connected users to send them the broadcast
            for(ServerWorker sw: server.getServerWorkers()){
                if(sw.getLogin() != user.getLogin()){
                    String msg = "msg " + getLogin() + " " + message + "\n";
                    sw.send(msg);
                }
            }
        }
        else{
            send("You do not have permission to broadcast a message \n");
        }
    }

    private void handleEditUserDescription(String[] response){
        if(user.getIsAdmin()){
            String newDescription = String.join(" ", Arrays.copyOfRange(response, 2, response.length));
            User user = null;
            for(ServerWorker sw: server.getServerWorkers()){
                if(sw.getLogin().equals(response[1])){
                    user = sw.getUser();
                }
            }
            if(user != null) user.setDescription(newDescription);
            else System.out.println("The user was not found");
        }
    }

    private void handleDeleteUser(String[] response) throws IOException {
        if(user.getIsAdmin()){
            for(ServerWorker sw: server.getServerWorkers()){
                if(sw.getLogin().equals(response[1])){
                    sw.handleLogoff();
                }
            }
            AdminUser admin = new AdminUser("", "");
            admin.deleteUser(response[1], "userStore.txt");
        }
    }

    private void handleKickUser(String[] response) throws IOException {
        if (user.getIsAdmin()) {
            User user = null;
            for (ServerWorker sw : server.getServerWorkers()) {
                if (sw.getLogin().equals(response[1])) {
                    user = sw.getUser();
                    sw.handleLogoff();
                }
            }
        }
    }
}
