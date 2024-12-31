//package com.client.liveowl.controller;
//
//import com.client.liveowl.JavaFxApplication;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.http.HttpStatus;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class SignUpController {
//
//    @FXML
//    private TextField email;
//
//    @FXML
//    private PasswordField password;
//
//    @FXML
//    private PasswordField confirmpassword;
//
//    @FXML
//    private TextField fullname;
//
//    @FXML
//    private DatePicker dateofbirth;
//
//    @FXML
//    private RadioButton male;
//
//    @FXML
//    private RadioButton female;
//
//    @FXML
//    private RadioButton student;
//
//    @FXML
//    private RadioButton teacher;
//
//    @FXML
//    private Label wrongFN;
//
//    @FXML
//    private Label wrongDOB;
//
//    @FXML
//    private Label wrongGender;
//
//    @FXML
//    private Label wrongSignup;
//
//    @FXML
//    private Label wrongUS;
//
//    @FXML
//    private Label wrongPW;
//
//    @FXML
//    private Label wrongCPW;
//
//    @FXML
//    private Label wrongEM;
//
//    @FXML
//    private CheckBox checkbox;
//
//    @FXML
//    private Button btnSignup;
//
//    @FXML
//    private Hyperlink hyperlink;
//
//    @FXML
//    public void userSignUp() throws IOException {
//        String emailText = email.getText();
//        String passwordText = password.getText();
//        String confirmPasswordText = confirmpassword.getText();
//        String fullnameText = fullname.getText();
//        Boolean ismale = male.isSelected();
//        Boolean isfemale = female.isSelected();
//        Boolean isstudent = student.isSelected();
//        Boolean isteacher = teacher.isSelected();
//        Boolean gender = false;
//        int role = 1;
//        LocalDate dateofbirths = null;
//        try {
//            dateofbirths = dateofbirth.getValue();
//
//        }catch (Exception e){
//
//        }
//        // Biểu thức chính quy để kiểm tra định dạng email
//        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
//        Pattern pattern = Pattern.compile(emailRegex);
//        Matcher matcher = pattern.matcher(emailText);
//
//        // Biểu thức chính quy để kiểm tra mật khẩu (chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt)
//        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
//        Pattern passwordPattern = Pattern.compile(passwordRegex);
//        Matcher passwordMatcher = passwordPattern.matcher(passwordText);
//
//        if(emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || fullnameText.isEmpty() || dateofbirths== null || (ismale == false && isfemale == false) || (isstudent == false && isteacher == false)) {
//            wrongSignup.setText("Hãy nhập đầy đủ thông tin");
//            return;
//        }else {
//            wrongSignup.setText("");
//        }
//
//        if (!matcher.matches()) {
//            wrongEM.setText("Email không hợp lệ.");
//            System.out.println("email không hợp lệ");
//            return;
//        } else {
//            wrongEM.setText("");
//        }
//
//
//        // Kiểm tra mật khẩu có đủ điều kiện không
//        if (passwordText.length() < 8 || !passwordMatcher.matches()) {
//            wrongPW.setText("Password phải có ít nhất 8 ký tự, bao gồm chữ, số và ký tự đặc biệt.");
//            return;
//        }else {
//            wrongPW.setText("");
//        }
//
//        // Kiểm tra password và confirmPassword có khớp không
//        if (!passwordText.equals(confirmPasswordText)) {
//            wrongCPW.setText("Password và Confirm Password không khớp.");
//            return;
//        }else {
//            wrongCPW.setText("");
//        }
//
//        // Kiểm tra xem checkbox đã được chọn hay chưa
//        if (!checkbox.isSelected()) {
//            wrongSignup.setText("Bạn phải chấp nhận các điều khoản.");
//            return;
//        }else {
//            wrongSignup.setText("");
//        }
//
//        if(ismale)
//            gender = true;
//        if(isstudent)
//            role = 2;
//        // Gọi API đăng ký nếu tất cả các điều kiện đều thỏa mãn
//        sendSignupRequest(emailText, passwordText,fullnameText, dateofbirths, gender, role);
//    }
//
//private void sendSignupRequest(String email, String password, String fullname, LocalDate dateofbirths, Boolean gender, int role) throws IOException {
//    String url = "http://localhost:9090/users/signup";
//
//    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//        HttpPost post = new HttpPost(url);
//
//        JSONObject json = new JSONObject();
//        json.put("email", email);
//        json.put("password", password);
//        json.put("role", role);
//        json.put("fullname", fullname);
//        json.put("dateofbirth", dateofbirths);
//        json.put("gender", gender);
//
//        post.setHeader("Content-type", "application/json; charset=UTF-8");
//        StringEntity entity = new StringEntity(json.toString(), "UTF-8");
//        post.setEntity(entity);
//
//        try (CloseableHttpResponse response = httpClient.execute(post)) {
//            int statusCode = response.getStatusLine().getStatusCode();
//            HttpEntity responseEntity = response.getEntity();
//            String responseString = EntityUtils.toString(responseEntity);
//            System.out.println("Response: " + responseString);
//
//            if (statusCode == HttpStatus.OK.value()) {
//                JavaFxApplication.changeScene("/views/Login.fxml");
//            } else {
//                System.out.println("Error: " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
//                wrongSignup.setText("Có lỗi xảy ra: " + response.getStatusLine().getReasonPhrase());
//            }
//        }
//    } catch (IOException | JSONException e) {
//        wrongSignup.setText("Có lỗi xảy ra khi gửi yêu cầu đăng ký.");
//        e.printStackTrace();
//    }
//}
//
//    @FXML
//    public void SignupToLogin() throws IOException {
//        JavaFxApplication javaFxApplication = new JavaFxApplication();
//        JavaFxApplication.changeScene("/views/Login.fxml");
//    }
////    public static  void main(String[] args) throws IOException {
////        SignUpController controller = new SignUpController();
////        controller.sendSignupRequest("gv2@gmail.com", "123456Hc@","Thai Hung", "", true, 2);
////    }
//}


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

public class SignUpController {

private static final String SIGNUP_URL = "http://localhost:9090/users/signup";
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
                JavaFxApplication.changeScene("/views/Login.fxml");
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
    JavaFxApplication.changeScene("/views/Login.fxml");
}
}
