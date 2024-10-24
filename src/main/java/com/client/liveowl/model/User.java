package com.client.liveowl.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.*;
import com.client.liveowl.util.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private static final String BASE_URI = "http://localhost:9090";
    private String email;
    private String password;
    private String role;
    private String profileImgLocation;
    private String fullName;
    private Boolean gender;
    private Date dateOfBirth;


    public User() {
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String id) {
        this.email = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImgLocation() {
        return profileImgLocation;
    }

    public void setProfileImgLocation(String profileImgLocation) {
        this.profileImgLocation = profileImgLocation;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public Boolean getGender() {
        return gender;
    }
    public void setGender(Boolean gender) {
        this.gender = gender;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public static boolean save(User newUser) {
        try{
            String uri = BASE_URI + "/users";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> req = new HttpEntity<>(newUser, httpHeaders);
            ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.POST, req, User.class);

            // Đăng kí thành công
            if(result.getStatusCode() == HttpStatus.CREATED){
                User savedUser =  result.getBody();
                System.out.println("User Registration successful for: " + savedUser.getEmail());
                return true;
            }
        }
        catch (Exception e){
            // Đăng kí thất bại
            String errMessage = "User Registration failed for: " + newUser.getEmail();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return false;
        }
        return false;
    }

    public static Optional<User> get(String id) {
        try{
            String uri = BASE_URI + "/users/" + id;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<User> responseEntity = restTemplate.getForEntity(uri,User.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                User user = responseEntity.getBody();
                System.out.println("Fetched User : " + user.getEmail());
                return Optional.of(user);
            }
        }
        catch(HttpClientErrorException.NotFound e){
            System.out.println("User not found: " + id);
            return Optional.empty();
        }

        return Optional.empty();
    }

    public static boolean isRegistered() throws Exception {
        String uri = BASE_URI+ "/users";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(uri, User[].class);
        User[] usersArr = responseEntity.getBody();
        if(usersArr.length > 0){
            System.out.println("User is registered");
            return true;
        }
        System.out.println("No user registered!");
        return false;
    }

    public static Optional getUserDetails(User user) throws Exception{
        // chưa sử lý
        return Optional.empty();
    }

    public static Optional<User> update(String id,User user) {
        try{
            String uri = BASE_URI + "/users/" + id;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> req = new HttpEntity<>(user, httpHeaders);
            ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.PUT, req, User.class);

            // User registration Successful
            if(result.getStatusCode() == HttpStatus.OK){
                User updatedUser =  result.getBody();
                System.out.println("User update successful for: " + updatedUser.getEmail());
                return Optional.of(updatedUser);
            }
        }
        catch (Exception e){
            // User registration failed
            String errMessage = "User update failed for: " + user.getEmail();
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
        }
        return Optional.empty();
    }

//    public static void delete(User user){
//        try{
//            System.out.println("Deleting User.....");
//            String uri = BASE_URI + "/users/" + user.getEmail();
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            restTemplate.delete(uri);
//
//            System.out.println("Deleted user: " + user.getEmail());
//        }
//        catch (Exception e){
//            System.out.println("Error deleting user !");
//            // Information dialog
//            AlertDialog alertDialog = new AlertDialog("Error","Error deleting user !", e.getMessage(), Alert.AlertType.ERROR);
//            alertDialog.showErrorDialog(e);
//        }
//    }


    public static Optional<String> resetPassword(String email) {
        try{
            System.out.println("Resetting password ...." + email);
            String uri = BASE_URI + "/users/forgot-password";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String,String> data = new HashMap<>();
            data.put("email",email);

            HttpEntity<Map<String,String>> req = new HttpEntity<>(data, httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, req,String.class);

            // Employee registration Successful
            if(result.getStatusCode() == HttpStatus.ACCEPTED){
                System.out.println("Reset Token sent to : " + email);
                return Optional.of(result.getBody());
            }
        }
        catch(Exception e){
            // Employee registration failed
            String errMessage = "Reset password failed for: " + email;
            System.out.println(errMessage);
            // Information dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static Optional<User> authorizeToken(String token) {
        try{
            System.out.println("Authorizing token ....");
            String uri = BASE_URI + "/users/reset-token";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String,String> data = new HashMap<>();
            data.put("reset-token",token);

            HttpEntity<Map<String,String>> req = new HttpEntity<>(data, httpHeaders);
            ResponseEntity<User> result = restTemplate.exchange(uri, HttpMethod.POST, req,User.class);

            // Employee registration Successful
            if(result.getStatusCode() == HttpStatus.ACCEPTED){
                System.out.println("Reset Token matched");
                User user = result.getBody();
                return Optional.of(user);
            }
        }
        catch(Exception e){
            // Employee registration failed
            String errMessage = "Reset token process failed !";
            System.out.println(errMessage);
            // Error dialog
            AlertDialog alertDialog = new AlertDialog("Error",errMessage,e.getMessage(), Alert.AlertType.ERROR);
            alertDialog.showErrorDialog(e);
            return Optional.empty();
        }
        return Optional.empty();
    }


}