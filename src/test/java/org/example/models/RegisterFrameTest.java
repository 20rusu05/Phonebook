package org.example.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFrameTest {

    private RegisterFrame registerFrame;

    @BeforeEach
    void setUp() {

        registerFrame = new RegisterFrame();
    }

    @Test
    void testRegisterWithEmptyFields() {
        JTextField usernameField = (JTextField) registerFrame.getContentPane().getComponent(1);
        JPasswordField passwordField = (JPasswordField) registerFrame.getContentPane().getComponent(3);

        usernameField.setText("");
        passwordField.setText("");

        JButton registerButton = (JButton) registerFrame.getContentPane().getComponent(5);
        registerButton.doClick();
    }
}
