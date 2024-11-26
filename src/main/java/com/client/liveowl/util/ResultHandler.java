package com.client.liveowl.util;

import com.client.liveowl.model.Result;
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

public class ResultHandler {
    private static final String BASE_URI = Authentication.getBaseUri();

    public static void addresult(Result result)
    {
        String url = BASE_URI + "/results/add";
        String examId = result.getExamId();
        String studentId = result.getStudentId();
        String linkKeyBoard = result.getLinkKeyBoard();
        String linkVideo = result.getLinkVideo();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        post.setHeader("Authorization", "Bearer " + Authentication.getToken());
        try
        {
            JSONObject json = new JSONObject();
            json.put("examId", examId);
            json.put("studentId", studentId);
            json.put("linkKeyBoard", linkKeyBoard);
            json.put("linkVideo", linkVideo);
            StringEntity entity = new StringEntity(json.toString(), "UTF-8");
            post.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    if (statusCode == HttpStatus.OK.value()) {
                        System.out.println("Them result thanh cong.");
                    } else {
                       System.out.println("Them That bai");
                    }
                } else {
                    System.out.println("Phản hồi từ server rỗng.");
                }
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}