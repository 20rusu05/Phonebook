package org.example.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

public class LoginFrameTest {

    private LoginFrame loginFrame;

    @BeforeEach
    void setUp() {
        loginFrame = new LoginFrame();
    }


    @Test
    void testLoginWithInvalidCredentials() {

        String username = "invalidUser";
        String password = "invalidPassword";

        boolean success = loginFrame.authenticateUser(ConnectDB.connect(), username, password);
        assertFalse(success, "Autentificarea nu ar trebui să reușească cu date invalide.");
    }

    @Test
    void testEmptyUsernameOrPassword() {
        String username = "";
        String password = "";

        boolean success = loginFrame.authenticateUser(ConnectDB.connect(), username, password);
        assertFalse(success, "Autentificarea nu ar trebui să reușească cu câmpuri goale.");
    }
}
