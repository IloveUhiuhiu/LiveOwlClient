package com.client.liveowl.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import java.io.IOException;


public class Authentication {
private static final String BASE_URI = "http://192.168.1.21:9090";
//private static final String BASE_URI = "http://localhost:9090";
private static boolean isAuthenticated;
private static String token;
private static int role;
private static String code;
public Authentication() {

}

public boolean isAuthenticated() {
    return isAuthenticated;
}

private void setAuthenticated(boolean authenticated) {
    isAuthenticated = authenticated;
}
public static String getCode() {
    return code;
}
public static void setCode(String code) {
    Authentication.code = code;
}

public static String getToken() {
    return token;
}

public void setToken(String token) {
    this.token = token;
}

public static int getRole() {
    return role;
}
public void setRole(int role) {
    this.role = role;
}
public static boolean login(String email, String password) throws Exception {
    String url = BASE_URI + "/users/signin?email=" + email + "&password=" + password;
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost post = new HttpPost(url);
    post.setHeader("Content-type", "application/x-www-form-urlencoded");
    CloseableHttpResponse response = httpClient.execute(post);
    int statusCode = response.getStatusLine().getStatusCode();
    System.out.println("statusCode: " + statusCode);
    if (statusCode == HttpStatus.OK.value()) {
        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity);
        JSONObject jsonResponse = new JSONObject(responseString);
        JSONObject data = jsonResponse.getJSONObject("data");

        token = data.getString("token");
        role = data.getInt("role");
        return true;
    } else  {
        return false;
    }
}
//    ObjectMapper objectMapper = new ObjectMapper();
//try {
//        UserData userData = objectMapper.readValue(jsonResponse.getJSONObject("data").toString(), UserData.class);
//        // Sử dụng userData ở đây
//        System.out.println("User ID: " + userData.getUserId());
//    } catch (JsonProcessingException e) {
//        // Xử lý lỗi
//        e.printStackTrace();
//    }
public boolean logout() {
    if (!this.isAuthenticated) {
        return false;
    }
    this.setAuthenticated(false);
    this.token = null;
    return true;
}
}