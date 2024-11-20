package com.client.liveowl.controller;

import com.client.liveowl.KeyLogger.GetFile;
import com.client.liveowl.util.UserHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class ResultController {

@FXML
private GridPane gridPane;

@FXML
private Button prevButton;

@FXML
private Button nextButton;

private List<String> accountIds;
private int currentPage = 0;
private final int itemsPerPage = 12;

public void initialize() {
    accountIds = UserHandler.getAllAccountID();
    updateGrid();
}

private void updateGrid() {
    gridPane.getChildren().clear();

    int start = currentPage * itemsPerPage;
    int end = Math.min(start + itemsPerPage, accountIds.size());

    for (int i = start; i < end; i++) {
        String accountId = accountIds.get(i);
        Pane pane = createPane(accountId);
        gridPane.add(pane, i % 3, (i / 3) % 4); // 3 cột
    }

    prevButton.setDisable(currentPage == 0);
    nextButton.setDisable(end >= accountIds.size());
}

private Pane createPane(String accountId) {
    Pane pane = new Pane();
    pane.setPrefSize(128.0, 59.0);
    pane.setStyle("-fx-background-color: white;");

    ImageView imageView = new ImageView(new Image(getClass().getResource("/images/document.png").toExternalForm()));
    imageView.setFitHeight(42.0);
    imageView.setFitWidth(41.0);
    imageView.setLayoutX(10.0);
    imageView.setLayoutY(14.0);
    pane.getChildren().add(imageView);

    Label label = new Label(accountId);
    label.setLayoutX(56.0);
    label.setLayoutY(11.0);
    label.setPrefSize(59.0, 17.0);
    pane.getChildren().add(label);

    Button seenButton = new Button();
    seenButton.setLayoutX(47.0);
    seenButton.setLayoutY(28.0);
    seenButton.setPrefSize(23.0, 28.0);
    seenButton.setStyle("-fx-background-color: white;");
    seenButton.setId("seen" + accountId);
    ImageView seenImage = new ImageView(new Image(getClass().getResource("/images/visible.png").toExternalForm()));
    seenImage.setFitHeight(20);
    seenImage.setFitWidth(20);
    seenButton.setGraphic(seenImage);
    seenButton.setOnAction(e ->{
        String id = seenButton.getId().replace("seen", "");
        GetFile f = new GetFile();
        f.downloadFile(id);
    });
    pane.getChildren().add(seenButton);

//    Button downloadButton = new Button();
//    downloadButton.setLayoutX(84.0);
//    downloadButton.setLayoutY(28.0);
//    downloadButton.setPrefSize(30.0, 28.0);
//    downloadButton.setStyle("-fx-background-color: white;");
//    downloadButton.setId("download" + accountId);
//    ImageView downloadImage = new ImageView(new Image(getClass().getResource("/images/download.png").toExternalForm()));
//    downloadImage.setFitHeight(20);
//    downloadImage.setFitWidth(20);
//    downloadButton.setGraphic(downloadImage);
//    downloadButton.setOnAction(e ->{
//        String id = downloadButton.getId().replace("download", "");
//        GetFile f = new GetFile();
//        f.downloadFile(id);
//    });
//    pane.getChildren().add(downloadButton);

    return pane;
}

@FXML
private void onPrevPage() {
    if (currentPage > 0) {
        currentPage--;
        updateGrid();
    }
}

@FXML
private void onNextPage() {
    if ((currentPage + 1) * itemsPerPage < accountIds.size()) {
        currentPage++;
        updateGrid();
    }
}

}

