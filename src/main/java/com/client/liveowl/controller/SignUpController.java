package com.client.liveowl.controller;

import com.client.liveowl.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static com.client.liveowl.AppConfig.BASE_URI;

public class SignUpController {

private static final String SIGNUP_URL = BASE_URI + "/users/signup";
private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

@FXML
private TextField email, fullname;

@FXML
private PasswordField password, confirmpassword;

@FXML
private DatePicker dateofbirth;

@FXML
private RadioButton male, female, student, teacher;

@FXML
private CheckBox checkbox;

@FXML
private Label wrongSignup, wrongEM, wrongPW, wrongCPW;

@FXML
public void userSignUp() {
    try {
        if (!validateInput()) return;

        String emailText = email.getText();
        String passwordText = password.getText();
        String fullnameText = fullname.getText();
        LocalDate dob = dateofbirth.getValue();
        boolean gender = male.isSelected();
        int role = student.isSelected() ? 2 : 1;

        sendSignupRequest(emailText, passwordText, fullnameText, dob, gender, role);
    } catch (Exception e) {
        wrongSignup.setText("Có lỗi xảy ra: " + e.getMessage());
        e.printStackTrace();
    }
}

private boolean validateInput() {
    String emailText = email.getText();
    String passwordText = password.getText();
    String confirmPasswordText = confirmpassword.getText();
    String fullnameText = fullname.getText();
    LocalDate dob = dateofbirth.getValue();

    // Kiểm tra thông tin bắt buộc
    if (emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || fullnameText.isEmpty() || dob == null || !isGenderSelected() || !isRoleSelected()) {
        wrongSignup.setText("Hãy nhập đầy đủ thông tin");
        return false;
    }

    // Kiểm tra email hợp lệ
    if (!Pattern.matches(EMAIL_REGEX, emailText)) {
        wrongEM.setText("Email không hợp lệ.");
        return false;
    } else {
        wrongEM.setText("");
    }

    // Kiểm tra mật khẩu hợp lệ
    if (!Pattern.matches(PASSWORD_REGEX, passwordText)) {
        wrongPW.setText("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ, số và ký tự đặc biệt.");
        return false;
    } else {
        wrongPW.setText("");
    }

    // Kiểm tra mật khẩu và xác nhận mật khẩu
    if (!passwordText.equals(confirmPasswordText)) {
        wrongCPW.setText("Xác nhận mật khẩu không khớp.");
        return false;
    } else {
        wrongCPW.setText("");
    }

    // Kiểm tra checkbox
    if (!checkbox.isSelected()) {
        wrongSignup.setText("Bạn phải chấp nhận các điều khoản.");
        return false;
    }

    wrongSignup.setText("");
    return true;
}

private boolean isGenderSelected() {
    return male.isSelected() || female.isSelected();
}

private boolean isRoleSelected() {
    return student.isSelected() || teacher.isSelected();
}

private void sendSignupRequest(String email, String password, String fullname, LocalDate dob, boolean gender, int role) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        HttpPost post = new HttpPost(SIGNUP_URL);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        json.put("fullname", fullname);
        json.put("dateofbirth", dob);
        json.put("gender", gender);
        json.put("role", role);

        post.setHeader("Content-type", "application/json; charset=UTF-8");
        post.setEntity(new StringEntity(json.toString(), "UTF-8"));

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.OK.value()) {
                JavaFxApplication.changeScene("/views/Login.fxml", "Login");
            } else {
                wrongEM.setText("Email đã tồn tại");
            }
        }
    } catch (Exception e  ) {
        wrongSignup.setText("Có lỗi xảy ra khi gửi yêu cầu đăng ký.");
        e.printStackTrace();
    }
}

@FXML
public void SignupToLogin() throws IOException {
    JavaFxApplication.changeScene("/views/Login.fxml", "Login");
}
}
