package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
        String emailinput = email.getText();
        String pass = password.getText();
        if (emailinput.isEmpty() || pass.isEmpty()) {
            wrongLogIn.setText("Please enter email and password");
            return;
        }

        // Sử dụng Task để gửi yêu cầu đăng nhập
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return sendLoginRequest(emailinput, pass);
            }
        };

        loginTask.setOnSucceeded(event -> {
            try {
                boolean result = loginTask.get();
                if (result) {
                    wrongLogIn.setText("Login successful!");
                    JavaFxApplication javaFxApplication = new JavaFxApplication();
                    JavaFxApplication.changeScene("/views/home.fxml");
                } else {
                    wrongLogIn.setText("email or password is incorrect!");
                }
            } catch (Exception e) {
                wrongLogIn.setText("Error processing response!");
            }
        });

        loginTask.setOnFailed(event -> {
            wrongLogIn.setText("Error connecting to server!");
        });

        new Thread(loginTask).start(); // Khởi động Task trong một luồng riêng
    }

    private boolean sendLoginRequest(String email, String password) throws IOException {
        String url = "http://localhost:9090/login/singin"; // Địa chỉ API đăng nhập

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            // Tạo JSON object để gửi trong body request
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            // Thiết lập header cho request
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            StringEntity entity = new StringEntity("email=" + email + "&password=" + password);
            post.setEntity(entity);

            // Gửi request và nhận phản hồi
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);

                // Kiểm tra kết quả đăng nhập
                return jsonResponse.getBoolean("data");
            }
        }
    }

    @FXML
    public void LoginToSignup() throws IOException {
        JavaFxApplication javaFxApplication = new JavaFxApplication();
        JavaFxApplication.changeScene("/views/Signup.fxml");
    }
}
