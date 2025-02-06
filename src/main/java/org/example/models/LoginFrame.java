package org.example.models;

import org.example.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            try (Connection conn = ConnectDB.connect()) {
                boolean success = authenticateUser(conn, username, password);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    new MainPageFrame();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });

        JButton closeButton = new JButton("Close");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(closeButton, gbc);

        closeButton.addActionListener((ActionEvent e) -> {
            new MainFrame();
            dispose();
        });

        setResizable(true);
        setVisible(true);
    }

    public boolean authenticateUser(Connection connection, String username, String password) {
        String query = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password"));
                user.setId(rs.getInt("id"));
                Session.setUser(user);
                return true;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error: " + ex.getMessage());
        }
        return false;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
