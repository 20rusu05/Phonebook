package org.example.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddContactFrameTest {

    private AddContactFrame addContactFrame;
    private Connection connection;

    @BeforeEach
    void setUp() {
        addContactFrame = new AddContactFrame();
        connection = ConnectDB.connect();
        assertNotNull(connection, "Database connection failed.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        String deleteQuery = "DELETE FROM contacts WHERE name = 'John Doe' AND phone = '123456789' AND email = 'john.doe@example.com'";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.executeUpdate();
        }
        connection.close();
    }

    @Test
    void testSaveContact_ValidFields_ShouldSaveToDatabase() throws SQLException {

        addContactFrame.nameField.setText("John Doe");
        addContactFrame.phoneField.setText("1234567890");
        addContactFrame.emailField.setText("john.doe@example.com");

        addContactFrame.saveContact();

        String query = "SELECT * FROM contacts WHERE name = ? AND phone = ? AND email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "John Doe");
            stmt.setString(2, "1234567890");
            stmt.setString(3, "john.doe@example.com");

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Contact was not saved in the database.");
                assertEquals("John Doe", rs.getString("name"));
                assertEquals("1234567890", rs.getString("phone"));
                assertEquals("john.doe@example.com", rs.getString("email"));
            }
        }
    }
}
