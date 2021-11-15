package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;

/**
 * Initial login frame
 * TODO ?extend so that host and port are input fields?
 */
public class LoginPane extends JFrame {

    private ChatClient client;
    private JTextField loginField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton createButton = new JButton("Create");
    private JLabel loginLabel = new JLabel("Login");
    private JLabel passwordLabel = new JLabel("Password");
    private JTextField responseText = new JTextField();


    public LoginPane() throws IOException {
        super("Login to Chat");
        this.client = new ChatClient("localhost", 8818);

        client.connect();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout(5,5));
        JPanel loginSubPanel = new JPanel(new FlowLayout());
        loginLabel.setPreferredSize(new Dimension(100,20));
        loginSubPanel.add(loginLabel);
        loginField.setPreferredSize(new Dimension(100,20));
        loginSubPanel.add(loginField);
        loginPanel.add(loginSubPanel,BorderLayout.NORTH);
        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordField.setPreferredSize(new Dimension(100,20));
        passwordLabel.setPreferredSize(new Dimension(100,20));
        passwordPanel.add(passwordLabel);

        passwordPanel.add(passwordField);
        loginPanel.add(passwordPanel,BorderLayout.CENTER);
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(responseText,BorderLayout.CENTER);
        responseText.setHorizontalAlignment(JTextField.CENTER);
        responseText.setEditable(false);
        responseText.setBackground(loginPanel.getBackground());
        responseText.setForeground(Color.RED);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(createButton);
        infoPanel.add(buttonPanel,BorderLayout.SOUTH);
        loginPanel.add(infoPanel,BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin(false);
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin(true);
            }
        });

        getContentPane().add(loginPanel,BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void doLogin(boolean isCreate) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (isCreate) {
            try {
                if (client.create(login, password)) {
                    responseText.setText(client.getResponseText());
                }
                else {
                    responseText.setText(client.getResponseText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (client.login(login, password)) {
                    loginSuccessful(login);
                } else {
                    // show error message

                    responseText.setText(client.getResponseText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginSuccessful(String login) {
        // bring up the user list window
        ChatPane userListPane = new ChatPane(client);
        JFrame frame = new JFrame("Chat Client: " + login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                try {
                    client.logoff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dispose();  // stop the program when windows 'x' is clicked
            }

        });
        setVisible(false);
        dispose();
    }

    public static void main(String[] args) throws IOException {

        LoginPane login = new LoginPane();
        login.setVisible(true);

    }

}
