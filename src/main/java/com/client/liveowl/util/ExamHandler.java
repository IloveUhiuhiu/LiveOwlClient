package com.client.liveowl.util;

import com.client.liveowl.model.Exam;
import com.client.liveowl.request.ExamRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static com.client.liveowl.AppConfig.*;
public class ExamHandler {
    private static final String BASE_URI = "http://"+ SERVER_HOST_NAME +":9090";
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
    public static Exam getExamById(String id) {
        String url = BASE_URI + "/exams/" + id;
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

                    // Chuyển đổi từ JSON sang đối tượng Exam
                    JSONObject jsonResponse = new JSONObject(responseString);
                    JSONObject dataObject = jsonResponse.getJSONObject("data");
                    return new ObjectMapper().readValue(dataObject.toString(), Exam.class);
                } else {
                    System.err.println("Failed to fetch exam: " + statusCode);
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
    public static Exam getExamByCode(String code) {
        String url = BASE_URI + "/exams/getByCode/" + code;
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

                    // Chuyển đổi từ JSON sang đối tượng Exam
                    JSONObject jsonResponse = new JSONObject(responseString);
                    JSONObject dataObject = jsonResponse.getJSONObject("data");
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    return objectMapper.readValue(dataObject.toString(), Exam.class);
                } else {
                    System.err.println("Failed to fetch exam: " + statusCode);
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
    public static boolean deleteExam(String id) {
        String url = BASE_URI + "/exams/delete/" + id;
        System.out.println("Token: " + Authentication.getToken());
        System.out.println("Request URL: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete delete = new HttpDelete(url);
            delete.setHeader("Authorization", "Bearer " + Authentication.getToken());

            try (CloseableHttpResponse response = httpClient.execute(delete)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Status Code: " + statusCode);

                if (statusCode == HttpStatus.OK.value()) {
                    return true;
                } else {
                    System.err.println("Failed to fetch exam: " + statusCode);
                    return false;
                }
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error creating HTTP client: " + e.getMessage());
            return false;
        }
    }

    public static boolean addExam(ExamRequest examRequest) {
        String url = BASE_URI + "/exams/add";
        System.out.println(Authentication.getToken());
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "Bearer " + Authentication.getToken());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String json = objectMapper.writeValueAsString(examRequest);
            post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("statusCode: " + statusCode);

                if (statusCode == HttpStatus.OK.value()) {
                  return true;
                } else {
                    System.err.println("Failed to fetch exams: " + statusCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch exams: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch exams: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean updateExam(ExamRequest examRequest, String id) {
        String url = BASE_URI + "/exams/update/" + id;
        System.out.println(Authentication.getToken());
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");
        put.setHeader("Authorization", "Bearer " + Authentication.getToken());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String json = objectMapper.writeValueAsString(examRequest);
            put.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(put)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("statusCode: " + statusCode);

                if (statusCode == HttpStatus.OK.value()) {
                    return true;
                } else {
                    System.err.println("Failed to fetch exams: " + statusCode);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch exams: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch exams: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }




}
