package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import client.ChatClient;

/**
 * Chat pane, currently only need one, but could be extended to create a new pane per room if
 * multi room is implemented.
 */
public class RoomChatPane extends JPanel implements MessageListener {

    private ChatClient client;
    private String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList(listModel);
    private JTextField inputField = new JTextField();
    private JLabel header = new JLabel();

    public RoomChatPane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);
        setLayout(new BorderLayout());
        add(header,BorderLayout.NORTH);
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField,BorderLayout.SOUTH);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    listModel.addElement("You: " + text);
                    inputField.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onMessage(String fromLogin, String msgBody) {
        String line = fromLogin + ": " + msgBody;
        listModel.addElement(line);
    }

    public void setHeader(String header) {
        this.header.setText("Room: " + header);
        listModel.addElement("-- Entered Room: " + header + " --");
    }
}
