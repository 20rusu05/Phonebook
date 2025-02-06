package org.example.models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddContactFrame extends JFrame {

    protected JTextField nameField;
    protected JTextField phoneField;
    protected JTextField emailField;
    private JCheckBox favoriteCheckBox;

    public AddContactFrame() {
        setTitle("Add Contact");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Favorite:"));
        favoriteCheckBox = new JCheckBox();
        add(favoriteCheckBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveContact();
            }
        });
        add(saveButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(closeButton);

        setVisible(true);
    }

    protected void saveContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        boolean isFavorite = favoriteCheckBox.isSelected();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits.", "Invalid Phone", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            JOptionPane.showMessageDialog(this, "Invalid email format. Example: example@domain.something", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Contact contact = new Contact(name, phone, email, isFavorite);

        String query = "INSERT INTO contacts (user_id, name, phone, email, isFavorite) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, Session.getUserId());
            stmt.setString(2, contact.getName());
            stmt.setString(3, contact.getPhone());
            stmt.setString(4, contact.getEmail());
            stmt.setBoolean(5, contact.isFavorite());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    contact.setId(generatedId);
                    JOptionPane.showMessageDialog(this, "Contact added successfully!");
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error adding contact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddContactFrame::new);
    }
}
