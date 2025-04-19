package SleepTracker;

import javafx.fxml.FXML;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;

public class SleepTrackerMainController {
    //Button menus
    @FXML
    private TextArea screen_output;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label status;


    @FXML
    void quit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void initialize() {
        screen_output.setEditable(false);
    }

    @FXML
    void recordLog() {
        GridPane recordPanel = new GridPane();
        recordPanel.setHgap(10);
        recordPanel.setVgap(10);
        recordPanel.setPadding(new Insets(20));
        recordPanel.add(new Label("Sleep quality: "), 0, 0);
        recordPanel.add(new TextField(), 1, 0);
        recordPanel.add(new Label("Bedtime (24:00): "), 0, 1);
        recordPanel.add(new TextField(), 1, 1);

        borderPane.setCenter(recordPanel);
    }

}

