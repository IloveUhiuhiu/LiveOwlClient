package com.client.liveowl.util;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class FormValidation {

    private Map<String,Boolean> formFields;

    public FormValidation() {
        formFields = new HashMap<String,Boolean>();
    }

    public Map<String, Boolean> getFormFields() {
        return formFields;
    }

    public static int iconSize;


    public static ValidatedResponse isName(String name, Label responseLabel){
        boolean valid = false;
        if(name == null){
            valid = false;
        }
        else {
            String exp = "^[A-Za-z\\s]+$";
            valid = name.matches(exp);
        }
        String msg = "Only alphabets and spaces allowed";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isPhoneNumber(String phoneNumber, Label responseLabel){
        boolean valid = false;
        if(phoneNumber == null){
            valid = false;
        }
        else {
            String exp = "((\\+*)((0[ -]*)*|((91 )*))((\\d{12})+|(\\d{10})+))|\\d{5}([- ]*)\\d{6}";
            valid = phoneNumber.matches(exp);
        }
        String msg = "Invalid Phone number";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isAddress(String address, Label responseLabel){
        boolean valid = false;
        if(address == null){
            valid = false;
        }
        else {
            valid = address.isEmpty() ? false : true;
        }
        String msg = "Please enter a valid address";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isIdProof(File file, Label responseLabel){
        boolean valid = false;
        if(file == null){
            valid = false;
        }
        else {
            String ext = FilenameUtils.getExtension(file.getAbsolutePath());
            System.out.println(ext);
            String exp = "^(jpg|jpeg|png|gif|bmp)$";
            valid = ext.matches(exp);
        }
        String msg = "Only jpeg /jpg / png / gif / bmp are allowed";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isPassword(String password, Label responseLabel){
        boolean valid = false;
        if(password == null){
            valid = false;
        }
        else {
            String exp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
            valid = password.matches(exp);
        }
        String msg = "Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:";
        return validationResponse(responseLabel,valid,msg);
    }


    public static ValidatedResponse isConfPassword(String password, String confPassword, Label responseLabel){
        boolean valid = false;
        if(confPassword == null){
            valid = false;
        }
        else {
            valid = password.equals(confPassword);
        }
        String msg = "Password does not match";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isInteger(String val, Label responseLabel){
        boolean valid = false;
        if(val == null){
            valid = false;
        }
        else {
            String exp = "^[1-9]\\d*$";
            valid = val.matches(exp);
        }
        String msg = "Only integer value";
        return validationResponse(responseLabel,valid,msg);
    }

    public static ValidatedResponse isDouble(String val, Label responseLabel){
        boolean valid = false;
        if(val == null){
            valid = false;
        }
        else {
            String exp = "^[+]?([0-9]+(?:[\\.][0-9]{1,2})?|\\.[0-9]+)$";
            valid = val.matches(exp);
        }
        String msg = "Only positive 2 decimal or integer value";
        return validationResponse(responseLabel,valid,msg);
    }


    public static ValidatedResponse isEmail(String val, Label responseLabel){
        boolean valid = false;
        if(val == null){
            valid = false;
        }
        else {
            String exp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                    + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            valid = val.matches(exp);
        }
        String msg = "Please enter a valid email address";
        return validationResponse(responseLabel,valid,msg);
    }




    // Simple methods with boolean returns
    public static boolean isImage(File file){
        boolean valid = false;
        if(file == null){
            valid = false;
        }
        else {
            String ext = FilenameUtils.getExtension(file.getAbsolutePath());
            String exp = "^(jpg|jpeg|png)$";
            valid = ext.matches(exp);
        }
        return valid;
    }

    public static ValidatedResponse validationResponse(Label responseLabel, boolean valid,String msg){
        FormValidation.iconSize = 24;
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());

        responseLabel.setText("");
        responseLabel.setGraphic(null);
        responseLabel.getStyleClass().clear();
        responseLabel.setWrapText(true);

        if(valid){
            responseLabel.setText("");
            IconNode correctIcon = new IconNode(GoogleMaterialDesignIcons.CHECK_CIRCLE);
            correctIcon.setIconSize(iconSize);
            correctIcon.setFill(Color.web("#152769"));
            responseLabel.setGraphic(correctIcon);
            responseLabel.getStyleClass().add("validate-success");
            responseLabel.setTooltip(new Tooltip(msg));
            responseLabel.setWrapText(true);
            return new ValidatedResponse(responseLabel,true);
        }
        else{
            responseLabel.setFont(new Font(responseLabel.getPrefHeight() * 0.50));
            responseLabel.setText(msg);
            IconNode wrongIcon = new IconNode(GoogleMaterialDesignIcons.HIGHLIGHT_OFF);
            wrongIcon.setIconSize(iconSize);
            wrongIcon.setFill(Color.web("#8c2c20"));
            responseLabel.setGraphic(wrongIcon);
            responseLabel.getStyleClass().add("validate-err");
            responseLabel.setTooltip(new Tooltip(msg));
            responseLabel.setWrapText(true);
            return new ValidatedResponse(responseLabel,false);
        }
    }
}