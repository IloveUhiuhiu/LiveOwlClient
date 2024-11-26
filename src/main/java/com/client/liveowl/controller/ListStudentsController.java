package com.client.liveowl.controller;

import com.client.liveowl.model.ResultItem;
import com.client.liveowl.util.Authentication;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ListStudentsController {
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
                        button.setStyle("-fx-font-weight: bold;-fx-background-color: #0C5776; -fx-background-radius: 8px; -fx-border-radius: 8px;");
                        button.setTextFill(Color.WHITE);
                        button.setFont(Font.font("System Bold", 12.0));
                        button.setOnAction(event -> {
                            ResultItem resultItem = getTableView().getItems().get(getIndex());
                            try {
                                Platform.runLater(() -> {
                                    try {
                                        openVideoPlayer("94e653ee","7662fa54");
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
                        button.setStyle("-fx-font-weight: bold;-fx-background-color: #2D99AE; -fx-background-radius: 8px; -fx-border-radius: 8px;");
                        button.setTextFill(Color.WHITE);
                        button.setFont(Font.font("System Bold", 12.0));
                        button.setOnAction(event -> {
                            ResultItem resultItem = getTableView().getItems().get(getIndex());
                            System.out.println("Bàn phím cho " + resultItem.getName());
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

        // Tạo danh sách dữ liệu
        ObservableList<ResultItem> data = FXCollections.observableArrayList(
                new ResultItem(1, "Nguyễn Văn A"),
                new ResultItem(2, "Trần Thị B"),
                new ResultItem(3, "Lê Văn C")
        );

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
        videoStage.show();
        VideoPlayerController controller = loader.getController();
        controller.initialize(code,clientId);
        System.out.println("Sucess");
    }

}