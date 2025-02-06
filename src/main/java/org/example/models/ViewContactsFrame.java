package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ViewContactsFrame extends JFrame {

    private JTable contactsTable;
    protected DefaultTableModel tableModel;

    public ViewContactsFrame() {
        setTitle("View Contacts");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Name");
        columnNames.add("Phone");
        columnNames.add("Email");
        columnNames.add("Favorite");

        tableModel = new DefaultTableModel(columnNames, 0);
        loadContacts();

        contactsTable = new JTable(tableModel);
        contactsTable.getColumnModel().getColumn(0).setMinWidth(0);
        contactsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        contactsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(contactsTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    protected void loadContacts() {
        tableModel.setRowCount(0);

        String query = "SELECT id, name, phone, email, isFavorite FROM contacts WHERE user_id = ?";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Contact contact = new Contact(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getBoolean("isFavorite")
                );

                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(contact.getName());
                row.add(contact.getPhone());
                row.add(contact.getEmail());
                row.add(contact.isFavorite() ? "Yes" : "No");
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
