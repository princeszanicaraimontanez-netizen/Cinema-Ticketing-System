/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.controller;

import com.movie.model.User;
import com.movie.service.UserService;
import com.movie.view.AdminDashboardView;
import com.movie.view.CustomerDashboardView;
import com.movie.view.LoginView;
import com.movie.view.RegisterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller handling user authentication and role-based window navigation.
 * 
 * Architectural Connection in System:
 * - Layer: Controller Layer (com.movie.controller)
 * - Upstream: Listens to UI events from LoginView (btnLogin, btnRegister).
 * - Downstream: Delegates credential verification to UserService.
 * - Navigation: Dispatches to AdminDashboardView or CustomerDashboardView upon successful login,
 *   or opens RegisterView for customer onboarding.
 */
public class LoginController implements ActionListener {

    private final LoginView view;
    private final UserService userService;

    public LoginController(LoginView view) {
        this.view = view;
        this.userService = new UserService();
        initListeners();
    }

    public LoginController(LoginView view, UserService userService) {
        this.view = view;
        this.userService = userService;
        initListeners();
    }

    private void initListeners() {
        view.getBtnLogin().addActionListener(this);
        view.getBtnRegister().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == view.getBtnLogin()) {
            handleLogin();
        } else if (src == view.getBtnRegister()) {
            openRegister();
        }
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        try {
            User user = userService.authenticate(username, password);
            if (user == null) {
                view.showMessage("Invalid username or password. Please try again.");
                view.clearFields();
                return;
            }

            view.showMessage("Welcome, " + user.getUsername() + "!");
            view.dispose();

            if (user.isAdmin()) {
                AdminDashboardView adminView = new AdminDashboardView(user);
                new AdminDashboardController(adminView);
                adminView.setVisible(true);
            } else {
                CustomerDashboardView customerView = new CustomerDashboardView(user);
                new CustomerDashboardController(customerView);
                customerView.setVisible(true);
            }
        } catch (IllegalArgumentException ex) {
            view.showMessage(ex.getMessage());
        } catch (Exception ex) {
            view.showMessage("System error during login: " + ex.getMessage());
        }
    }

    private void openRegister() {
        RegisterView regView = new RegisterView();
        new RegisterController(regView);
        regView.setVisible(true);
    }
}
