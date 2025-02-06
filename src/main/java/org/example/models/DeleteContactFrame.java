package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeleteContactFrame extends JFrame {

    protected JTable contactTable;
    private JButton deleteButton, closeButton;
    private DefaultTableModel model;

    public DeleteContactFrame() {
        setTitle("Delete Contact");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        contactTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        deleteButton = new JButton("Delete Contact");
        closeButton = new JButton("Close");

        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteContact();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        loadContacts();

        setVisible(true);
    }

    protected void loadContacts() {
        String query = "SELECT id, name, phone, email, isFavorite FROM contacts WHERE user_id = ?";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();

            model = new DefaultTableModel();
            model.addColumn("Name");
            model.addColumn("Phone");
            model.addColumn("Email");
            model.addColumn("Favorite");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                boolean isFavorite = rs.getBoolean("isFavorite");
                String favoriteStatus = isFavorite ? "Yes" : "No";

                model.addRow(new Object[]{name, phone, email, favoriteStatus});
            }

            contactTable.setModel(model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading contacts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public JTable getContactTable() {
        return contactTable;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    protected void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {

            String name = (String) contactTable.getValueAt(selectedRow, 0);
            String phone = (String) contactTable.getValueAt(selectedRow, 1);
            String email = (String) contactTable.getValueAt(selectedRow, 2);

            String query = "DELETE FROM contacts WHERE name = ? AND phone = ? AND email = ? AND user_id = ?";
            try (Connection conn = ConnectDB.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, name);
                stmt.setString(2, phone);
                stmt.setString(3, email);
                stmt.setInt(4, Session.getUserId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Contact deleted!");
                    loadContacts();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting contact.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting contact: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteContactFrame::new);
    }
}
