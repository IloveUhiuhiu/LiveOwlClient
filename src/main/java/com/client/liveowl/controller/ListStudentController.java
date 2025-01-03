package com.client.liveowl.controller;

import com.client.liveowl.keylogger.ProcessGetFile;
import com.client.liveowl.model.ResultDTO;
import com.client.liveowl.model.ResultItem;
import com.client.liveowl.socket.StudentSocket;
import com.client.liveowl.socket.TeacherSocket;
import com.client.liveowl.util.AlertDialog;
import com.client.liveowl.util.Authentication;
import com.client.liveowl.util.ResultHandler;
import com.client.liveowl.video.ProcessPlayVideo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class ListStudentController {
    @FXML
    private TableView<ResultItem> tableContent;
    @FXML
    private TableColumn<ResultItem, Integer> stt;
    @FXML
    private TableColumn<ResultItem, String> name;
    @FXML
    private TableColumn<ResultItem, String> video;
    @FXML
    private TableColumn<ResultItem, String> keyboard;
    @FXML
    private TableColumn<ResultItem, String> detail;
    public static String examId;
    public static String code;

    @FXML
    public void initialize() {

        stt.setCellValueFactory(new PropertyValueFactory<>("STT")); // Liên kết với thuộc tính stt
        stt.setCellFactory(column -> createCenteredCell());
        name.setCellValueFactory(new PropertyValueFactory<>("Name")); // Liên kết với thuộc tính name
        // Cột Chi Tiết

        detail.setCellFactory(new Callback<TableColumn<ResultItem, String>, TableCell<ResultItem, String>>() {
            @Override
            public TableCell<ResultItem, String> call(TableColumn<ResultItem, String> param) {
                return new TableCell<ResultItem, String>() {
                    private final Button button = new Button("Chi Tiết");
                    {
                        addHover(button);
                        button.setStyle("-fx-font-weight: bold;-fx-background-color: #001C44; -fx-background-radius: 8px; -fx-border-radius: 8px;");
                        button.setTextFill(Color.WHITE);
                        button.setFont(Font.font("System Bold", 12.0));
                        button.setOnAction(event -> {
                            ResultItem resultItem = getTableView().getItems().get(getIndex());
                            System.out.println("Chi tiết cho " + resultItem.getName());
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : button);
                        setStyle("-fx-alignment: CENTER;");
                    }
                };
            }
        });

        // Cột Video
        video.setCellFactory(new Callback<TableColumn<ResultItem, String>, TableCell<ResultItem, String>>() {
            @Override
            public TableCell<ResultItem, String> call(TableColumn<ResultItem, String> param) {
                return new TableCell<ResultItem, String>() {
                    private final Button button = new Button("Video");
                    {
                        addHover(button);
                        button.setStyle("-fx-font-weight: bold;-fx-background-color: #0C5776; -fx-background-radius: 8px; -fx-border-radius: 8px;");
                        button.setTextFill(Color.WHITE);
                        button.setFont(Font.font("System Bold", 12.0));
                        button.setOnAction(event -> {
                            ResultItem resultItem = getTableView().getItems().get(getIndex());
                            try {
                                Platform.runLater(() -> {
                                    try {
                                        openVideoPlayer(resultItem.getCode(), resultItem.getStudentId());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Video cho " + resultItem.getName());
                        });
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : button);
                        setStyle("-fx-alignment: CENTER;");
                    }
                };
            }
        });

        // Cột Bàn Phím
        keyboard.setCellFactory(new Callback<TableColumn<ResultItem, String>, TableCell<ResultItem, String>>() {
            @Override
            public TableCell<ResultItem, String> call(TableColumn<ResultItem, String> param) {
                return new TableCell<ResultItem, String>() {
                    private final Button button = new Button("Bàn Phím");
                    {
                        addHover(button);
                        button.setStyle("-fx-font-weight: bold;-fx-background-color: #2D99AE; -fx-background-radius: 8px; -fx-border-radius: 8px;");
                        button.setTextFill(Color.WHITE);
                        button.setFont(Font.font("System Bold", 12.0));
                        button.setOnAction(event -> {
                            ResultItem resultItem = getTableView().getItems().get(getIndex());
                            System.out.println("Bàn phím cho " + resultItem.getName());
                            ProcessGetFile getFile = new ProcessGetFile();
                            //getFile.downloadFile(resultItem.getCode(),resultItem.getStudentId());
                            getFile.downloadFile(resultItem.getStudentId(), resultItem.getCode());
                        });
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : button);
                        setStyle("-fx-alignment: CENTER;");
                    }
                };
            }
        });

        List<ResultDTO> resultList = ResultHandler.getResultsByExamId(examId);

        int cnt = 1;
        ObservableList<ResultItem> data = FXCollections.observableArrayList();

        for (ResultDTO rs: resultList) {
            data.add(new ResultItem(
                    cnt,
                    code,
                    rs.getStudentId(),
                    rs.getName()
            ));
            cnt++;
        }
        tableContent.setItems(data);
    }



    private void addHover(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            button.setCursor(javafx.scene.Cursor.HAND);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });
    }

    private <T> TableCell<ResultItem, T> createCenteredCell() {
        TableCell<ResultItem, T> cell = new TableCell<ResultItem, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle("-fx-alignment: CENTER;"); // Căn giữa cho nội dung
            }
        };
        return cell;
    }
    private void openVideoPlayer(String code, String clientId) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/VideoPlayer.fxml"));
        Scene scene = new Scene(loader.load());
        Stage videoStage = new Stage();
        videoStage.setTitle("Video Player");
        videoStage.setScene(scene);
        VideoPlayerController controller = loader.getController();
        ProcessPlayVideo watchedVideo = new ProcessPlayVideo();
        videoStage.setOnCloseRequest(event -> {
            event.consume();
            AlertDialog alertDialog = new AlertDialog("Xác nhận thoát " ,null,"Bạn có chắc chắn muốn thoát không?", Alert.AlertType.CONFIRMATION);
            Alert alert = alertDialog.getConfirmationDialog();
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    controller.sendNotificationToServer("exit");
                    watchedVideo.cleanResource();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    videoStage.close();
                }
            });
        });
        videoStage.show();
        controller.initialize(code,clientId,watchedVideo);
        System.out.println("Success");
    }


}