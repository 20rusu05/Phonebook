package org.example.models;

import javax.swing.*;
import java.awt.*;

public class MainPageFrame extends JFrame {

    public MainPageFrame() {
        setTitle("Main Page");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton viewContactsButton = new JButton("View Contacts");
        viewContactsButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(viewContactsButton, gbc);
        viewContactsButton.addActionListener(e -> new ViewContactsFrame());

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(addContactButton, gbc);
        addContactButton.addActionListener(e -> new AddContactFrame());

        JButton editContactButton = new JButton("Edit Contact");
        editContactButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(editContactButton, gbc);
        editContactButton.addActionListener(e -> new EditContactFrame());

        JButton deleteContactButton = new JButton("Delete Contact");
        deleteContactButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(deleteContactButton, gbc);
        deleteContactButton.addActionListener(e -> new DeleteContactFrame());

        JButton favoriteContactsButton = new JButton("Favorite Contacts");
        favoriteContactsButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(favoriteContactsButton, gbc);
        favoriteContactsButton.addActionListener(e -> new FavoriteContactsFrame());

        JButton searchContactButton = new JButton("Search Contact");
        searchContactButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(searchContactButton, gbc);
        searchContactButton.addActionListener(e -> new SearchContactFrame());

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (Session.isLoggedIn()) {
                new MainPageFrame();
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        });
    }
}
