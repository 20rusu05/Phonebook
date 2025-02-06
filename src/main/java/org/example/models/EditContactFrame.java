package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class EditContactFrame extends JFrame {

    protected JTable contactsTable;
    private DefaultTableModel tableModel;

    protected JTextField nameField;
    protected JTextField phoneField;
    protected JTextField emailField;
    protected JCheckBox favoriteCheckBox;
    protected int selectedContactId = -1;

    public EditContactFrame() {
        setTitle("Edit Contact");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Name");
        columnNames.add("Phone");
        columnNames.add("Email");
        columnNames.add("Favorite");

        tableModel = new DefaultTableModel(columnNames, 0);
        loadAllContacts();

        contactsTable = new JTable(tableModel);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = contactsTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedContactId = (int) tableModel.getValueAt(selectedRow, 0);
                    String name = (String) tableModel.getValueAt(selectedRow, 1);
                    String phone = (String) tableModel.getValueAt(selectedRow, 2);
                    String email = (String) tableModel.getValueAt(selectedRow, 3);
                    String favoriteStatus = (String) tableModel.getValueAt(selectedRow, 4);

                    nameField.setText(name);
                    phoneField.setText(phone);
                    emailField.setText(email);
                    favoriteCheckBox.setSelected("Yes".equals(favoriteStatus));
                }
            }
        });

        contactsTable.getColumnModel().getColumn(0).setMinWidth(0);
        contactsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        contactsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(contactsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(6, 2));

        editPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        editPanel.add(nameField);

        editPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        editPanel.add(phoneField);

        editPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        editPanel.add(emailField);

        editPanel.add(new JLabel("Favorite:"));
        favoriteCheckBox = new JCheckBox();
        editPanel.add(favoriteCheckBox);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveContact();
            }
        });

        editPanel.add(new JLabel());
        editPanel.add(saveButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        editPanel.add(new JLabel());
        editPanel.add(closeButton);

        add(editPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    protected void loadAllContacts() {
        String query = "SELECT id, name, phone, email, isFavorite FROM contacts WHERE user_id = ?";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                boolean isFavorite = rs.getBoolean("isFavorite");
                String favoriteStatus = isFavorite ? "Yes" : "No";

                Contact contact = new Contact(name, phone, email, isFavorite);
                contact.setId(id);
                Vector<Object> row = new Vector<>();
                row.add(contact.getId());
                row.add(contact.getName());
                row.add(contact.getPhone());
                row.add(contact.getEmail());
                row.add(favoriteStatus);
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void saveContact() {
        if (selectedContactId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = nameField.getText();
        String newPhone = phoneField.getText();
        String newEmail = emailField.getText();
        boolean isFavorite = favoriteCheckBox.isSelected();

        if (newName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPhone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits.", "Invalid Phone", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            JOptionPane.showMessageDialog(this, "Invalid email format. Example: example@domain.something", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Contact updatedContact = new Contact(newName, newPhone, newEmail, isFavorite);
        updatedContact.setId(selectedContactId);

        String query = "UPDATE contacts SET name = ?, phone = ?, email = ?, isFavorite = ? WHERE id = ?";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, updatedContact.getName());
            stmt.setString(2, updatedContact.getPhone());
            stmt.setString(3, updatedContact.getEmail());
            stmt.setBoolean(4, updatedContact.isFavorite());
            stmt.setInt(5, updatedContact.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Contact updated successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating contact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        loadAllContacts();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditContactFrame::new);
    }
}
