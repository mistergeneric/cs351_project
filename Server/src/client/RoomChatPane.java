package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Chat pane, currently only need one, but could be extended to create a new pane per room if
 * multi room is implemented.
 */
public class RoomChatPane extends JPanel implements MessageListener {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JTextField inputField = new JTextField();

    public RoomChatPane(ChatClient client, String login) {

        client.addMessageListener(this);
        setLayout(new BorderLayout());
        JList<String> messageList = new JList<>(listModel);
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField,BorderLayout.SOUTH);
        inputField.addActionListener(e -> {
            try {
                String text = inputField.getText();
                client.msg(login, text);
                listModel.addElement("You: " + text);
                inputField.setText("");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }


    @Override
    public void onMessage(String fromLogin, String msgBody) {
        String line = fromLogin + ": " + msgBody;
        listModel.addElement(line);
    }

}
