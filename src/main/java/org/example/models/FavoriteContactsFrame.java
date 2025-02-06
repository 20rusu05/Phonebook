package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class FavoriteContactsFrame extends JFrame {

    private JTable contactTable;
    private JButton closeButton;
    private JButton removeFavoriteButton;

    public FavoriteContactsFrame() {
        setTitle("Favorite Contacts");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        contactTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        closeButton = new JButton("Close");
        removeFavoriteButton = new JButton("Remove from Favorites");

        buttonPanel.add(closeButton);
        buttonPanel.add(removeFavoriteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        removeFavoriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFromFavorites();
            }
        });

        loadFavoriteContacts();

        setVisible(true);
    }

    private void loadFavoriteContacts() {
        String query = "SELECT id, name, phone, email, isFavorite FROM contacts WHERE user_id = ? AND isFavorite = TRUE";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Name");
            model.addColumn("Phone");
            model.addColumn("Email");

            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                boolean isFavorite = rs.getBoolean("isFavorite");

                Contact contact = new Contact(name, phone, email, isFavorite);
                contact.setId(rs.getInt("id"));

                Vector<Object> row = new Vector<>();
                row.add(contact.getName());
                row.add(contact.getPhone());
                row.add(contact.getEmail());
                model.addRow(row);
            }

            contactTable.setModel(model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading contacts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeFromFavorites() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) contactTable.getValueAt(selectedRow, 0);
            String query = "UPDATE contacts SET isFavorite = FALSE WHERE name = ? AND user_id = ?";

            try (Connection conn = ConnectDB.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, name);
                stmt.setInt(2, Session.getUserId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Contact removed from favorites!");
                    loadFavoriteContacts();
                } else {
                    JOptionPane.showMessageDialog(this, "Error removing contact from favorites.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error removing contact from favorites: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FavoriteContactsFrame::new);
    }
}
