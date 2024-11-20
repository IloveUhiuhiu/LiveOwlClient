package com.client.liveowl.util;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {
    private static final String BASE_URI = "http://localhost:9090";
   // private static final String BASE_URI = "http://10.10.26.160:9090";

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
//    public  static  void main(String[] args) {
//        List<String> allid = getAllAccountID();
//        for (String id : allid) {
//            System.out.println(id);
//        }
//    }
}