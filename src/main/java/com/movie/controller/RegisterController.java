/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.controller;

import com.movie.service.UserService;
import com.movie.view.RegisterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller handling user registration and validation.
 * 
 * Architectural Connection in System:
 * - Layer: Controller Layer (com.movie.controller)
 * - Upstream: Listens to UI events from RegisterView (btnRegister, btnCancel).
 * - Downstream: Invokes UserService to validate credentials and hash passwords.
 * - Persistence: UserService forwards the sanitized entity to UserDAO for MySQL insertion.
 */
public class RegisterController implements ActionListener {

    private final RegisterView view;
    private final UserService userService;

    public RegisterController(RegisterView view) {
        this.view = view;
        this.userService = new UserService();
        initListeners();
    }

    public RegisterController(RegisterView view, UserService userService) {
        this.view = view;
        this.userService = userService;
        initListeners();
    }

    private void initListeners() {
        view.getBtnRegister().addActionListener(this);
        view.getBtnCancel().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnRegister()) {
            handleRegister();
        } else if (e.getSource() == view.getBtnCancel()) {
            view.dispose();
        }
    }

    private void handleRegister() {
        String username = view.getUsername();
        String fullName = view.getFullName();
        String password = view.getPassword();
        String confirm  = view.getConfirm();

        try {
            boolean success = userService.register(username, fullName, password, confirm);
            if (success) {
                view.showMessage("Account successfully created! You may now sign in.");
                view.dispose();
            } else {
                view.showMessage("Registration failed. Please check your inputs and try again.");
            }
        } catch (IllegalArgumentException ex) {
            view.showMessage(ex.getMessage());
        } catch (Exception ex) {
            view.showMessage("Error saving account: " + ex.getMessage());
        }
    }
}
