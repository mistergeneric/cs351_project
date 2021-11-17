package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import client.ChatClient;

/**
 * Main Frame, holds view of rooms, users in current room and buttons to send commands
 */
public class ChatPane extends JPanel {

    private ChatClient client;
    private RoomChatPane roomChatPane;

    public ChatPane(ChatClient client)  {
        this.client = client;
        setLayout(new BorderLayout());

        roomChatPane = new RoomChatPane(client, "");
        add(roomChatPane, BorderLayout.CENTER);
        add(generateCommandPanel(),BorderLayout.NORTH);
        setVisible(true);
    }

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

    private Component getAdminActionPanel() {
        JPanel adminActionPanel = new JPanel();
        adminActionPanel.setLayout(new FlowLayout());
        JButton broadcast = new JButton("Broadcast");
        broadcast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = JOptionPane.showInputDialog("Broadcast Message:");
                if (message != null && !message.equals("")) {
                    try {
                        client.send("broadcast " + message + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JButton delete = new JButton("Delete User");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = JOptionPane.showInputDialog("Enter user to delete");
                if (message != null && !message.equals("")) {
                    try {
                        client.send("delete " + message + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JButton modifyOtherUser = new JButton("Modify User Bio");
        modifyOtherUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = JOptionPane.showInputDialog("Enter user to modify");
                String bio = JOptionPane.showInputDialog("Enter new bio");
                if (user != null) {
                    try {
                        client.send("modify " + user + " " + (bio==null ? "" : bio) + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        adminActionPanel.add(broadcast);
        adminActionPanel.add(delete);
        adminActionPanel.add(modifyOtherUser);
        return adminActionPanel;
    }

    public Component getUserActionPanel() {
        JPanel userActions = new JPanel();
        userActions.setLayout(new FlowLayout());

        JButton friendRequest = new JButton("Friend Req");
        friendRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String friend = JOptionPane.showInputDialog("Enter username you would like to send friend request to");
                if (friend != null && !friend.equals("")) {
                    try {
                        client.send("friend " + friend + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        userActions.add(friendRequest);
        JButton viewDetails = new JButton("View Details");
        viewDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter username to get details from");
                String msg = "details " + username + "\n";
                try {
                    client.send(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        userActions.add(viewDetails);

        JButton viewFriends = new JButton("View Friends");
        viewFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "friendsInChat\n";
                try {
                    client.send(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        userActions.add(viewFriends);

        JButton editDetails = new JButton("Edit Details");
        editDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JCheckBox checkbox = new JCheckBox("Hide likes?");
                    Object[] params = {checkbox, "Enter new description:"};
                    String description = JOptionPane.showInputDialog(null,params,"Likes",JOptionPane.OK_OPTION);
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
            }
        });
        userActions.add(editDetails);


        JButton likeButton = new JButton("Like");
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String liked = JOptionPane.showInputDialog("Enter username to like");
                if (liked != null && !liked.equals("")) {
                    try {
                        client.send("like " + liked + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        userActions.add(likeButton);

        JButton logoff = new JButton("Logoff");
        logoff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.logoff();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        userActions.add(logoff);

        return userActions;
    }

    public Component getRoomActionPanel() {

        JPanel roomActions = new JPanel();
        roomActions.setLayout(new FlowLayout());

        JButton joinRoom = new JButton("Join Room");
        joinRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomname = JOptionPane.showInputDialog("Enter room to Join/Create");
                if (roomname != null && !roomname.equals("")) {
                    try {
                        client.joinRoom(roomname);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        roomActions.add(joinRoom);

        JButton leaveRoom = new JButton("Leave Room");
        leaveRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.send("leave\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        roomActions.add(leaveRoom);

        JButton viewRooms = new JButton("View Rooms");
        viewRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.send("showrooms\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        roomActions.add(viewRooms);

        JButton usersInRoom = new JButton("View users in room");
        usersInRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String roomname = JOptionPane.showInputDialog("Which room would you like to view users from:");
                    if (roomname != null && !roomname.equals("")) {
                        client.send("viewUsers " + roomname + "\n");
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });
        roomActions.add(usersInRoom);

        return roomActions;
    }

}
