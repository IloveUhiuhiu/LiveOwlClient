package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmpassword;

    @FXML
    private Label wrongSignup;

    @FXML
    private Label wrongUS;

    @FXML
    private Label wrongPW;

    @FXML
    private Label wrongCPW;

    @FXML
    private Label wrongEM;

    @FXML
    private CheckBox checkbox;

    @FXML
    private Button btnSignup;

    @FXML
    private Hyperlink hyperlink;

    @FXML
    public void userSignUp() throws IOException {
        String usernameText = username.getText();
        String emailText = email.getText();
        String passwordText = password.getText();
        String confirmPasswordText = confirmpassword.getText();

        // Biểu thức chính quy để kiểm tra định dạng email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailText);

        // Biểu thức chính quy để kiểm tra mật khẩu (chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt)
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(passwordText);

        if(usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            wrongSignup.setText("Hãy nhập đầy đủ thông tin");
            return;
        }else {
            wrongSignup.setText("");
        }
        // Kiểm tra độ dài của username
        if (usernameText.length() < 5) {
            wrongUS.setText("Username quá ngắn");
            return;
        }else {
            wrongUS.setText("");
        }

        if (usernameText.length() > 20) {
            wrongUS.setText("Username quá dài");
            return;
        }else {
            wrongUS.setText("");
        }

        // Kiểm tra xem username có bắt đầu bằng chữ hay không
        if (!Character.isLetter(usernameText.charAt(0))) {
            wrongUS.setText("Username phải bắt đầu bằng chữ cái.");
            return;
        }else {
            wrongUS.setText("");
        }

        // Kiểm tra định dạng email
        if (!matcher.matches()) {
            wrongEM.setText("Email không hợp lệ.");
            return;
        }else {
            wrongEM.setText("");
        }

        // Kiểm tra username và password có trùng nhau không
        if (usernameText.equals(passwordText)) {
            wrongPW.setText("Password không được trùng với Username.");
            return;
        }else {
            wrongPW.setText("");
        }

        // Kiểm tra mật khẩu có đủ điều kiện không
        if (passwordText.length() < 8 || !passwordMatcher.matches()) {
            wrongPW.setText("Password phải có ít nhất 8 ký tự, bao gồm chữ, số và ký tự đặc biệt.");
            return;
        }else {
            wrongPW.setText("");
        }

        // Kiểm tra password và confirmPassword có khớp không
        if (!passwordText.equals(confirmPasswordText)) {
            wrongCPW.setText("Password và Confirm Password không khớp.");
            return;
        }else {
            wrongCPW.setText("");
        }

        // Kiểm tra xem checkbox đã được chọn hay chưa
        if (!checkbox.isSelected()) {
            wrongSignup.setText("Bạn phải chấp nhận các điều khoản.");
            return;
        }else {
            wrongSignup.setText("");
        }

        // Gọi API đăng ký nếu tất cả các điều kiện đều thỏa mãn
        sendSignupRequest(usernameText, emailText, passwordText, confirmPasswordText);
    }

    private void sendSignupRequest(String username, String email, String password, String confirmPassword) throws IOException {
        String url = "http://localhost:9090/login/singup";

        // Tạo client HTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            // Tạo JSON object để gửi thông tin
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);
            json.put("confirmpassword", confirmPassword);
            json.put("role_id", 2);

            // Thiết lập header cho request
            post.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);

            // Gửi request và nhận phản hồi
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity =  response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);

                if (jsonResponse.getBoolean("data") == true) {
                    JavaFxApplication javaFxApplication = new JavaFxApplication();
                    JavaFxApplication.changeScene("/views/Login.fxml");
                } else {
                    wrongSignup.setText("username hoặc email đã tồn tại");
                }
            }
        }
    }

    public boolean checkUserName(String username) {
        if((username.length() >= 5) && (username.length() <= 20)) {
            for(int i = 0; i< username.length(); i++) {
                if(Character.isLetter(username.charAt(0))) {
                    wrongSignup.setText("username cannot begin with numbers or special characters");
                    return  false;
                }
            }
            return true;
        } else if (username.length() <5) {
            wrongSignup.setText("username is too short");
            return  false;
        }
        else {
            wrongSignup.setText("username is too long");
            return  false;
        }
    }

    @FXML
    public void SignupToLogin() throws IOException {
        JavaFxApplication javaFxApplication = new JavaFxApplication();
        JavaFxApplication.changeScene("/views/Login.fxml");
    }
}
