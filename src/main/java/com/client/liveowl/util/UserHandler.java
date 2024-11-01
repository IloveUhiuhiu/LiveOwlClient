package com.client.liveowl.util;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class UserHandler {
    private static final String BASE_URI = "http://localhost:9090";

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
//    public  static  void main(String[] args) {
//        String userId = getUserId();
//        System.out.println(userId);
//    }
}

