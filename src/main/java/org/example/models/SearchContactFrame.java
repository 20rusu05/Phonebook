package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchContactFrame extends JFrame {

    private JTextField nameField, phoneField, emailField;
    private JButton searchButton;
    private JTable resultsTable;

    public SearchContactFrame() {
        setTitle("Search Contact");
        setSize(1500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Name: "));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("Phone: "));
        searchPanel.add(phoneField);
        searchPanel.add(new JLabel("Email: "));
        searchPanel.add(emailField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        setVisible(true);
    }

    private void performSearch() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() && phone.isEmpty() && email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one search criterion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT id, name, phone, email, isFavorite FROM contacts WHERE user_id = ? AND ");
        boolean firstCondition = true;

        if (!name.isEmpty()) {
            queryBuilder.append("name LIKE ? ");
            firstCondition = false;
        }

        if (!phone.isEmpty()) {
            if (!firstCondition) queryBuilder.append("AND ");
            queryBuilder.append("phone LIKE ? ");
            firstCondition = false;
        }

        if (!email.isEmpty()) {
            if (!firstCondition) queryBuilder.append("AND ");
            queryBuilder.append("email LIKE ? ");
        }

        List<Contact> contactsList = new ArrayList<>();

        int currentUserId = Session.getUserId();

        try (Connection connection = ConnectDB.connect();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            int parameterIndex = 1;
            statement.setInt(parameterIndex++, currentUserId);

            if (!name.isEmpty()) {
                statement.setString(parameterIndex++, "%" + name + "%");
            }
            if (!phone.isEmpty()) {
                statement.setString(parameterIndex++, "%" + phone + "%");
            }
            if (!email.isEmpty()) {
                statement.setString(parameterIndex++, "%" + email + "%");
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Contact contact = new Contact(
                            resultSet.getString("name"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getBoolean("isFavorite")
                    );
                    contact.setId(resultSet.getInt("id"));
                    contactsList.add(contact);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Email", "Favorite"}, 0);
        for (Contact contact : contactsList) {
            String favoriteStatus = contact.isFavorite() ? "Yes" : "No";
            tableModel.addRow(new Object[]{contact.getName(), contact.getPhone(), contact.getEmail(), favoriteStatus});
        }

        resultsTable.setModel(tableModel);

        if (contactsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No contacts found matching the search criteria.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SearchContactFrame::new);
    }
}
