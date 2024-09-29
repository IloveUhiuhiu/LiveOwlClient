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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

   /* @FXML
    private TextField username;*/

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmpassword;

    @FXML
    private TextField fullname;

    @FXML
    private DatePicker dateofbirth;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private RadioButton student;

    @FXML
    private RadioButton teacher;

    @FXML
    private Label wrongFN;

    @FXML
    private Label wrongDOB;

    @FXML
    private Label wrongGender;

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
        String emailText = email.getText();
        String passwordText = password.getText();
        String confirmPasswordText = confirmpassword.getText();
        String fullnameText = fullname.getText();
        String dateofbirthText = dateofbirth.getValue().toString();
        Boolean ismale = male.isSelected();
        Boolean isfemale = female.isSelected();
        Boolean isstudent = student.isSelected();
        Boolean isteacher = teacher.isSelected();
        Boolean gender = false;
        int role = 1;


        // Biểu thức chính quy để kiểm tra định dạng email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailText);

        // Biểu thức chính quy để kiểm tra mật khẩu (chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt)
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(passwordText);

        if(emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || fullnameText.isEmpty() || dateofbirthText.isEmpty() || (ismale == false && isfemale == false) || (isstudent == false && isteacher == false)) {
            wrongSignup.setText("Hãy nhập đầy đủ thông tin");
            return;
        }else {
            wrongSignup.setText("");
        }
//        // Kiểm tra độ dài của username
//        if (usernameText.length() < 5) {
//            wrongUS.setText("Username quá ngắn");
//            return;
//        }else {
//            wrongUS.setText("");
//        }
//
//        if (usernameText.length() > 20) {
//            wrongUS.setText("Username quá dài");
//            return;
//        }else {
//            wrongUS.setText("");
//        }
//
//        // Kiểm tra xem username có bắt đầu bằng chữ hay không
//        if (!Character.isLetter(usernameText.charAt(0))) {
//            wrongUS.setText("Username phải bắt đầu bằng chữ cái.");
//            return;
//        }else {
//            wrongUS.setText("");
//        }

        // Kiểm tra định dạng email
        if (!matcher.matches()) {
            wrongEM.setText("Email không hợp lệ.");
            return;
        }else {
            wrongEM.setText("");
        }

        // Kiểm tra username và password có trùng nhau không
//        if (usernameText.equals(passwordText)) {
//            wrongPW.setText("Password không được trùng với Username.");
//            return;
//        }else {
//            wrongPW.setText("");
//        }

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

        if(ismale)
            gender = true;
        if(isteacher)
            role = 2;


        // Gọi API đăng ký nếu tất cả các điều kiện đều thỏa mãn
        sendSignupRequest(emailText, passwordText,fullnameText, dateofbirthText, gender, role);
    }

    private void sendSignupRequest(String email, String password, String fullname, String dateofbirth, Boolean gender, int role) throws IOException {
        String url = "http://localhost:9090/login/singup";  // Địa chỉ API đăng ký tài khoản

        // Tạo client HTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            // Tạo đối tượng JSON để chứa dữ liệu
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("role", role);
            json.put("fullname", fullname);
            json.put("dateofbirth", dateofbirth);
            json.put("gender", gender);

            // Đặt header cho request
            post.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);

            // Gửi request và xử lý phản hồi
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);

                if (jsonResponse.getBoolean("data")) {
                    // Điều hướng về màn hình đăng nhập nếu đăng ký thành công
                    JavaFxApplication.changeScene("/views/Login.fxml");
                } else {
                    wrongSignup.setText("Email đã tồn tại.");
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | JSONException e) {
            wrongSignup.setText("Có lỗi xảy ra khi gửi yêu cầu đăng ký.");
            e.printStackTrace();
        }
    }




    @FXML
    public void SignupToLogin() throws IOException {
        JavaFxApplication javaFxApplication = new JavaFxApplication();
        JavaFxApplication.changeScene("/views/Login.fxml");
    }
}
