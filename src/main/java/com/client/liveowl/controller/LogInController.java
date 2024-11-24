package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.client.liveowl.util.Authentication;
import java.io.IOException;

public class LogInController {
    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Label wrongLogIn;

    @FXML
    private Button button;

    @FXML
    public void userLogIn() {
        String emailInput = email.getText();
        String pass = password.getText();
        System.out.println(emailInput + ", " + pass);
        if (emailInput.isEmpty() || pass.isEmpty()) {
            wrongLogIn.setText("Nhập email và password!");
            return;
        }

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return Authentication.login(emailInput, pass);
            }
        };
        loginTask.setOnSucceeded(event -> {
            try {
                boolean result = loginTask.get();
                if (result) {
                    wrongLogIn.setText("Đăng nhập thành công!");
                    if (Authentication.getRole() == 1) {
                        JavaFxApplication.changeScene("/views/Home.fxml");
                    } else {
                        JavaFxApplication.changeScene("/views/Student.fxml");
                    }

                } else {
                    wrongLogIn.setText("Email hoặc mật khẩu không hợp lệ!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                wrongLogIn.setText("Lỗi phản hồi!");
            }
        });

        loginTask.setOnFailed(event -> {
            wrongLogIn.setText("Lỗi khi kết nối server!");
        });

        new Thread(loginTask).start();
    }

    @FXML
    public void LoginToSignup() throws IOException {
        JavaFxApplication.changeScene("/views/Signup.fxml");
    }

}

