package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


/**
 * Initial login frame
 * creates server, login and password fields to be used with ChatClient
 */
public class LoginPane extends JFrame {

    private ChatClient client;
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField serverTextField = new JTextField("localhost");
    private final JTextField responseText = new JTextField();

    /**
     * Constructor
     */
    public LoginPane() {
        super("Login to Chat");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // nested panels for displaying
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout(5,5));
        JPanel fieldsPanel = new JPanel(new BorderLayout(5,5));

        JPanel serverPanel = new JPanel(new FlowLayout());
        JLabel serverLabel = new JLabel("Server Address");
        serverLabel.setPreferredSize(new Dimension(100,20));
        serverTextField.setPreferredSize(new Dimension(100,20));
        serverPanel.add(serverLabel);
        serverPanel.add(serverTextField);
        fieldsPanel.add(serverPanel, BorderLayout.NORTH);

        JPanel loginSubPanel = new JPanel(new FlowLayout());
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setPreferredSize(new Dimension(100,20));
        loginSubPanel.add(loginLabel);
        loginField.setPreferredSize(new Dimension(100,20));
        loginSubPanel.add(loginField);
        fieldsPanel.add(loginSubPanel,BorderLayout.CENTER);

        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordField.setPreferredSize(new Dimension(100,20));
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setPreferredSize(new Dimension(100,20));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        fieldsPanel.add(passwordPanel,BorderLayout.SOUTH);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(responseText,BorderLayout.CENTER);
        responseText.setHorizontalAlignment(JTextField.CENTER);
        responseText.setEditable(false);
        responseText.setBackground(loginPanel.getBackground());
        responseText.setForeground(Color.RED);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        JButton createButton = new JButton("Create");
        buttonPanel.add(createButton);
        infoPanel.add(buttonPanel,BorderLayout.SOUTH);
        loginPanel.add(fieldsPanel,BorderLayout.CENTER);
        loginPanel.add(infoPanel,BorderLayout.SOUTH);


        loginButton.addActionListener(e -> {
            doConnect(serverTextField.getText());
            doLogin(false);
        });

        createButton.addActionListener(e -> {
            doConnect(serverTextField.getText());
            doLogin(true);
        });

        getContentPane().add(loginPanel,BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    /**
     * Attempt connection to server
     * @param server String IP Address
     */
    private void doConnect(String server) {

        try {
            this.client = new ChatClient(server,8818);
            client.connect();
        } catch (IOException e) {
            responseText.setText(e.getMessage());
        }
    }

    /**
     * Attempt login
     * @param isCreate
     */
    private void doLogin(boolean isCreate) {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (isCreate) {
            try {
                if (!client.create(login, password)) {
                    responseText.setText(client.getResponseText());
                }
                else {
                    loginSuccessful(login);
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

    /**
     * If login is successful
     * @param login
     */
    private void loginSuccessful(String login) {
        // bring up the user list window
        client.setLogin(login);
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

    public static void main(String[] args) {

        LoginPane login = new LoginPane();
        login.setVisible(true);

    }

}
