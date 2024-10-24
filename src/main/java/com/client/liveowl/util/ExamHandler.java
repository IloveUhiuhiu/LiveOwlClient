package com.client.liveowl.util;

import com.client.liveowl.model.Exam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExamHandler {
    private static final String BASE_URI = "http://localhost:9090";
    public static List<Exam> getExamsByAccount() {
        String url = BASE_URI + "/exams/all";
        System.out.println(Authentication.getToken());
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url); // Sử dụng HttpGet thay vì HttpPost

        // Thêm header chứa token
        get.setHeader("Authorization", "Bearer " + Authentication.getToken());

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode: " + statusCode);

            if (statusCode == HttpStatus.OK.value()) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONArray dataArray = jsonResponse.getJSONArray("data");

                List<Exam> exams = new ArrayList<>(); // Danh sách chứa các đối tượng Exam

                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Định dạng cho LocalDateTime

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject examJson = dataArray.getJSONObject(i);
                    Exam exam = new Exam();
                    exam.setExamId(examJson.getString("examId"));
                    exam.setNameOfExam(examJson.getString("nameOfExam"));
                    exam.setSubjectOfExam(examJson.getString("subjectOfExam"));

                    // Chuyển đổi chuỗi thành LocalDateTime
                    LocalDateTime startTime = LocalDateTime.parse(examJson.getString("startTimeOfExam"), formatter);
                    exam.setStartTimeOfExam(startTime); // Giả sử bạn đã thay đổi kiểu dữ liệu trong lớp Exam

                    exam.setDurationOfExam(examJson.getInt("durationOfExam"));
                    exam.setCodeOfExam(examJson.getString("codeOfExam"));
                    exams.add(exam); // Thêm vào danh sách
                }
                return exams; // Trả về danh sách bài thi
            } else {
                System.err.println("Failed to fetch exams: " + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
