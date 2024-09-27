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
    private String jwtToken;

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
                    // Chuyển sang trang home
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

        new Thread(loginTask).start();
    }

    private boolean sendLoginRequest(String email, String password) throws IOException {
        String url = "http://localhost:9090/login/singin";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            StringEntity entity = new StringEntity("email=" + email + "&password=" + password);
            post.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                System.out.println("kq " + responseString);
                JSONObject jsonResponse = new JSONObject(responseString);

                if (jsonResponse.getBoolean("issucess")) {
                    // Lưu JWT token
                    jwtToken = jsonResponse.getString("data");
                    return true;
                }
                return false;
            }
        }
    }

    @FXML
    public void LoginToSignup() throws IOException {
        JavaFxApplication.changeScene("/views/Signup.fxml");
    }

    public String getJwtToken() {
        return jwtToken;
    }
}

