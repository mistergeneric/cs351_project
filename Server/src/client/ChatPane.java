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
    private DefaultListModel<String> roomListModel;
    private JList<String> roomList;
    private DefaultListModel<String> roomUserListModel;
    private JList<String> roomUserList;
    private RoomChatPane roomChatPane;

    public ChatPane(ChatClient client)  {
        this.client = client;
        setLayout(new BorderLayout());

        roomChatPane = new RoomChatPane(client, "Lobby");
        add(roomChatPane, BorderLayout.CENTER);

        add(generateRoomUserList(), BorderLayout.WEST);
        add(generateRoomPanel(), BorderLayout.NORTH);

        setVisible(true);
    }

    private JPanel generateCommandPanel() {
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new GridLayout(2,2));
        JButton friendRequest = new JButton("Friend Req");
        friendRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String friend = roomUserList.getSelectedValue();
                if (friend != null) {
                    if (!friend.equals(client.getLogin())) {
                        int opt = JOptionPane.showConfirmDialog(null, "Send friend request to " + friend, "Friend", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            String msg = "friend " + friend + " " + client.getLogin() + "\n";
                            try {
                                client.send(msg);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Unable to friend yourself", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        commandPanel.add(friendRequest);
        JButton viewDetails = new JButton("View Details");
        viewDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String viewing = roomUserList.getSelectedValue();
                String msg = "details " + viewing + "\n";
                try {
                    client.send(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        commandPanel.add(viewDetails);

        JButton viewFriends = new JButton("View Friends");
        viewFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "friends\n";
                try {
                    client.send(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        commandPanel.add(viewFriends);

        JButton editDetails = new JButton("Edit Details");
        editDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.send("mydetails\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        commandPanel.add(editDetails);
        return commandPanel;
    }

    private JPanel generateRoomPanel() {
        JPanel roomPane = new JPanel();
        roomPane.setLayout(new BorderLayout());
        roomPane.add(new JLabel("Rooms:"),BorderLayout.NORTH);
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomPane.add(new JScrollPane(roomList),BorderLayout.CENTER);
        JPanel roomButtonPane = new JPanel();
        roomButtonPane.setLayout(new GridLayout(4,1));
        JButton joinRoom = new JButton("Join");
        joinRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = JOptionPane.showInputDialog("Enter room to Join/Create");
                try {
                    client.joinRoom(roomName);
                    roomUserListModel.removeAllElements();
                    client.send("requestRoom\n");
                    roomChatPane.setHeader(roomName);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        roomButtonPane.add(joinRoom);
        JButton viewRoom = new JButton("View");
        viewRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = roomList.getSelectedValue();
                //TODO get list of users and display separately
            }
        });
        roomButtonPane.add(viewRoom);
        JButton leave = new JButton("Leave");
        leave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.joinRoom("Lobby");
                roomChatPane.setHeader("Lobby");
            }
        });
        roomButtonPane.add(leave);
        roomPane.add(roomButtonPane,BorderLayout.EAST);
        return roomPane;
    }

    private Component generateRoomUserList() {
        roomUserListModel = new DefaultListModel<>();
        roomUserList = new JList<>();
        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BorderLayout());
        roomListPanel.setPreferredSize(new Dimension(200,400));
        roomListPanel.add(new JLabel("Room Occupants"), BorderLayout.NORTH);
        roomListPanel.add(new JScrollPane(roomUserList), BorderLayout.CENTER);
        roomListPanel.add(generateCommandPanel(), BorderLayout.SOUTH);
        return roomListPanel;
    }
}
