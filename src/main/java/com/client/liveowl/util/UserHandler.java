package com.client.liveowl.util;
import com.client.liveowl.model.Exam;
import com.client.liveowl.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static com.client.liveowl.AppConfig.BASE_URI;

@Service
public class UserHandler {


    public static String getUserId() {
        String url = BASE_URI + "/users/detail";

        String token = Authentication.getToken();
        System.out.println("token: " + token);
        //   String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoyLCJzdWIiOiJzdDRAZ21haWwuY29tIn0.atCALsZ0MhIbozc8w-4A-O3sLfVuxL528M7UIofmTrk";
        if (token == null || token.isEmpty()) {
            System.err.println("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return null;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("StatusCode: " + statusCode);

            if (statusCode == HttpStatus.OK.value()) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);

                // Lấy ID từ JSON
                String accountId = jsonResponse.getJSONObject("data").getString("accountId");
                System.out.println("User ID: " + accountId);
                return accountId; // Trả về ID người dùng
            } else {
                System.err.println("Lỗi khi lấy thông tin tài khoản: " + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllAccountID() {
        String url = BASE_URI + "/users/allaccoutid";
        System.out.println(Authentication.getToken());
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJzdWIiOiJjQGdtYWlsLmNvbSJ9.qO1hErx1vP51kB0TtksN53lj2miR-xDzGEt8y8Lq-Dg";
        // Thêm header chứa token
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode: " + statusCode);

            if (statusCode == HttpStatus.OK.value()) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONArray dataArray = jsonResponse.getJSONArray("data");

                List<String> allaccountid = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
                    allaccountid.add(dataArray.getString(i));
                }
                return allaccountid;
            } else {
                System.err.println("Failed to fetch allaccountid: " + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getDetailUser(){
        String url = BASE_URI + "/users/detail";

        String token = Authentication.getToken();
        System.out.println("token: " + token);
     //   String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJzdWIiOiJndjZAZ21haWwuY29tIn0.F7sgNbU_fpERAARCXrf59bFvrMKzBqnyWJZe_Bk5mjY";
        if (token == null || token.isEmpty()) {
            System.err.println("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return null;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("StatusCode: " + statusCode);

            if (statusCode == HttpStatus.OK.value()) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);

                User user = new User();
                user.setFullName(jsonResponse.getJSONObject("data").getString("fullName"));
                user.setEmail(jsonResponse.getJSONObject("data").getString("email"));
                user.setDateOfBirth(LocalDate.parse(jsonResponse.getJSONObject("data").getString("dateOfBirth")));
                user.setGender(Boolean.valueOf(jsonResponse.getJSONObject("data").getString("gender")));
                user.setProfileImgLocation(jsonResponse.getJSONObject("data").getString("profile"));
                user.setRole(jsonResponse.getJSONObject("data").getString("role"));
                return user;
            } else {
                System.err.println("Lỗi khi lấy thông tin tài khoản: " + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendImage(String profile) {
        String url = BASE_URI + "/users/uploadavt";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        post.setHeader("Authorization", "Bearer " + Authentication.getToken());

        try {
            JSONObject json = new JSONObject();
            json.put("image", profile);
            StringEntity entity = new StringEntity(json.toString(), "UTF-8");
            post.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    if (statusCode == HttpStatus.OK.value()) {
                        System.out.println("Thay đổi thành công.");
                    } else {
                        System.out.println("Lỗi: " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
                        System.out.println("Chi tiết: " + responseString);
                    }
                } else {
                    System.out.println("Phản hồi từ server rỗng.");
                }
            }
        } catch (JSONException e) {
            System.out.println("Lỗi khi tạo JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi yêu cầu HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean sendInfor(String name, String email, LocalDate dateofbirth, Boolean gender) {
        String url = BASE_URI + "/users/updateinfo";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        post.setHeader("Authorization", "Bearer " + Authentication.getToken());

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("email", email);
            json.put("dateofbirth", dateofbirth);
            json.put("gender", gender);
            StringEntity entity = new StringEntity(json.toString(), "UTF-8");
            post.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    if (statusCode == HttpStatus.OK.value()) {
                        System.out.println("Thay đổi thành công.");
                        return true;
                    } else {
                        System.out.println("Lỗi: " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
                        System.out.println("Chi tiết: " + responseString);
                        return false;
                    }
                } else {
                    System.out.println("Phản hồi từ server rỗng.");
                    return false;
                }
            }
        } catch (JSONException e) {
            System.out.println("Lỗi khi tạo JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi yêu cầu HTTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static User getInforUserById(String userId) {
        String url = BASE_URI + "/users/getuserbyid/" + userId;
        System.out.println("Token: " + Authentication.getToken());
        System.out.println("Request URL: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + Authentication.getToken());

            try (CloseableHttpResponse response = httpClient.execute(get)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Status Code: " + statusCode);

                if (statusCode == HttpStatus.OK.value()) {
                    HttpEntity responseEntity = response.getEntity();
                    String responseString = EntityUtils.toString(responseEntity);

                    JSONObject jsonResponse = new JSONObject(responseString);
                    JSONObject dataObject = jsonResponse.getJSONObject("data");
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    return objectMapper.readValue(dataObject.toString(), User.class);
                } else {
                    System.err.println("Failed to fetch user: " + statusCode);
                    return null;
                }
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                return null;
            } catch (JSONException e) {
                System.err.println("JSON error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.err.println("Error creating HTTP client: " + e.getMessage());
            return null;
        }
    }

//    public  static  void main(String[] args) {
////        List<String> allid = getAllAccountID();
////        for (String id : allid) {
////            System.out.println(id);
////        }
//        User user = getDetailUser();
//        System.out.println(user.getEmail());
//        System.out.println(user.getFullName());
//        System.out.println(user.getDateOfBirth());
//        System.out.println(user.getGender());
//        System.out.println(user.getProfileImgLocation());
//
//    }
}