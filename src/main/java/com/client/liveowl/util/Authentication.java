package com.client.liveowl.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import static com.client.liveowl.AppConfig.BASE_URI;


public class Authentication {

private static boolean isAuthenticated;
private static String token;
private static int role;
private static String code;
public static String usedId;
public Authentication() {
}

public boolean isAuthenticated() {
    return isAuthenticated;
}
public static void setAuthenticated(boolean authenticated) {
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
public static int getRole() {
    return role;
}
public static String getUserId() {return usedId;}

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
        usedId = data.getString("userId");
        token = data.getString("token");
        role = data.getInt("role");
        System.out.println("Id là" + usedId);
        return true;
    } else  {
        return false;
    }
}
public boolean logout() {
    if (!this.isAuthenticated) {
        return false;
    }
    this.setAuthenticated(false);
    this.token = null;
    this.role = 0;
    this.usedId = null;
    return true;
}
}