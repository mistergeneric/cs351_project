package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


/**
 * Main Frame, holds view of rooms, users in current room and buttons to send commands
 */
public class ChatPane extends JPanel {

    private final ChatClient client;

    public ChatPane(ChatClient client)  {
        this.client = client;
        setLayout(new BorderLayout());

        RoomChatPane roomChatPane = new RoomChatPane(client, "");
        add(roomChatPane, BorderLayout.CENTER);
        add(generateCommandPanel(),BorderLayout.NORTH);
        setVisible(true);
    }

    /**
     * Panel for controls
     * @return JPanel
     */
    private JPanel generateCommandPanel() {
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new GridLayout(3,1));
        commandPanel.add(getRoomActionPanel());
        commandPanel.add(getUserActionPanel());
        if (client.getLogin().equals("admin")) {
            commandPanel.add(getAdminActionPanel());
        }
        return commandPanel;
    }

    /**
     * Panel for admin actions
     * @return JPanel
     */
    private Component getAdminActionPanel() {
        JPanel adminActionPanel = new JPanel();
        adminActionPanel.setLayout(new FlowLayout());
        JButton broadcast = new JButton("Broadcast");
        broadcast.addActionListener(e -> {
            String message = JOptionPane.showInputDialog("Broadcast Message:");
            if (message != null && !message.equals("")) {
                try {
                    client.send("broadcast " + message + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton delete = new JButton("Delete User");
        delete.addActionListener(e -> {
            String message = JOptionPane.showInputDialog("Enter user to delete");
            if (message != null && !message.equals("")) {
                try {
                    client.send("delete " + message + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton kickUser = new JButton("Kick User");
        kickUser.addActionListener(e -> {
            String message = JOptionPane.showInputDialog("Enter user to kick");
            if (message != null && !message.equals("")) {
                try {
                    client.send("kick " + message + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton modifyOtherUser = new JButton("Modify User Bio");
        modifyOtherUser.addActionListener(e -> {
            String user = JOptionPane.showInputDialog("Enter user to modify");
            String bio = JOptionPane.showInputDialog("Enter new bio");
            if (user != null) {
                try {
                    client.send("modify " + user + " " + (bio==null ? "" : bio) + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        adminActionPanel.add(broadcast);
        adminActionPanel.add(delete);
        adminActionPanel.add(modifyOtherUser);
        adminActionPanel.add(kickUser);
        return adminActionPanel;
    }

    /**
     * Panel for user actions
     * @return JPanel
     */
    public Component getUserActionPanel() {
        JPanel userActions = new JPanel();
        userActions.setLayout(new FlowLayout());

        JButton friendRequest = new JButton("Friend Req");
        friendRequest.addActionListener(e -> {
            String friend = JOptionPane.showInputDialog("Enter username you would like to send friend request to");
            if (friend != null && !friend.equals("")) {
                try {
                    client.send("friend " + friend + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        userActions.add(friendRequest);
        JButton viewDetails = new JButton("View Details");
        viewDetails.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter username to get details from");
            String msg = "details " + username + "\n";
            try {
                client.send(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        userActions.add(viewDetails);

        JButton viewFriends = new JButton("View Friends");
        viewFriends.addActionListener(e -> {
            String msg = "friendsInChat\n";
            try {
                client.send(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        userActions.add(viewFriends);

        JButton editDetails = new JButton("Edit Details");
        editDetails.addActionListener(e -> {
            try {
                JCheckBox checkbox = new JCheckBox("Hide likes?");
                Object[] params = {checkbox, "Enter new description:"};
                String description = JOptionPane.showInputDialog(null,params,"Likes",JOptionPane.INFORMATION_MESSAGE);
                boolean hideLikes = checkbox.isSelected();
                client.send("update " + description + "\n");
                if (hideLikes) {
                    client.send("likes hide\n");
                } else {
                    client.send("likes show\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        userActions.add(editDetails);


        JButton likeButton = new JButton("Like");
        likeButton.addActionListener(e -> {
            String liked = JOptionPane.showInputDialog("Enter username to like");
            if (liked != null && !liked.equals("")) {
                try {
                    client.send("like " + liked + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        userActions.add(likeButton);

        JButton logoff = new JButton("Logoff");
        logoff.addActionListener(e -> {
            try {
                client.logoff();
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        userActions.add(logoff);

        return userActions;
    }

    /**
     * Panel for room actions
     * @return JPanel
     */
    public Component getRoomActionPanel() {

        JPanel roomActions = new JPanel();
        roomActions.setLayout(new FlowLayout());

        JButton joinRoom = new JButton("Join Room");
        joinRoom.addActionListener(e -> {
            String roomname = JOptionPane.showInputDialog("Enter room to Join/Create");
            if (roomname != null && !roomname.equals("")) {
                try {
                    client.joinRoom(roomname);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });
        roomActions.add(joinRoom);

        JButton leaveRoom = new JButton("Leave Room");
        leaveRoom.addActionListener(e -> {
            try {
                client.send("leave\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        roomActions.add(leaveRoom);

        JButton viewRooms = new JButton("View Rooms");
        viewRooms.addActionListener(e -> {
            try {
                client.send("showrooms\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        roomActions.add(viewRooms);

        JButton usersInRoom = new JButton("View users in room");
        usersInRoom.addActionListener(e -> {
            try {
                String roomname = JOptionPane.showInputDialog("Which room would you like to view users from:");
                if (roomname != null && !roomname.equals("")) {
                    client.send("viewUsers " + roomname + "\n");
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        });
        roomActions.add(usersInRoom);

        return roomActions;
    }

}
