package com.client.liveowl.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertDialog {
    private String title;
    private String headerText;
    private String contentText;
    private String exception;
    private Alert.AlertType type;


    public AlertDialog(String title, String headerText, String contentText, Alert.AlertType type) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = contentText;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void showErrorDialog(Exception e) {
        Alert alert = new Alert(this.type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Ghi lại lỗi
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Chi tiết lỗi :");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Util.getScreenWidth() * 0.6);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Thêm phẩn mở rộng
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public void showInformationDialog(){
        Alert alert = new Alert(this.type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public Alert getConfirmationDialog(){
        Alert alert = new Alert(this.type);
        alert.setTitle(this.title);
        alert.setHeaderText(this.headerText);
        alert.setContentText(this.contentText);
        return alert;
    }
}
