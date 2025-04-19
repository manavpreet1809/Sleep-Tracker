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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;
import java.util.ArrayList;

public class SleepTrackerMainController {
    //Button menus
    @FXML
    private TextArea screen_output;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label status;
    @FXML
    private User user;


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
        TextField sleepQualityField = new TextField();
        recordPanel.add(sleepQualityField, 1, 0);

        recordPanel.add(new Label("Room temperature (in °C)"), 0, 1);
        TextField roomTempField = new TextField();
        recordPanel.add(roomTempField, 1, 1);

        recordPanel.add(new Label("Sleep duration (in hrs)"), 0, 2);
        TextField sleepDurationField = new TextField();
        recordPanel.add(sleepDurationField, 1, 2);

        recordPanel.add(new Label("Time spent doing cardio over the day (in minutes)"), 0, 3);
        TextField cardioTimeField = new TextField();
        recordPanel.add(cardioTimeField, 1, 3);

        recordPanel.add(new Label("Time spent doing napping during the day (in minutes)"), 0, 4);
        TextField napTimeField = new TextField();
        recordPanel.add(napTimeField, 1, 4);

        recordPanel.add(new Label("Screen time (in hrs)"), 0, 5);
        TextField screenTimeField = new TextField();
        recordPanel.add(screenTimeField, 1, 5);

        recordPanel.add(new Label("Age of mattress in months (only enter once)"), 0, 6);
        TextField mattressAgeField = new TextField();
        recordPanel.add(mattressAgeField, 1, 6);

        recordPanel.add(new Label("Water intake (in mL)"), 0, 7);
        TextField waterField = new TextField();
        recordPanel.add(waterField, 1, 7);

        recordPanel.add(new Label("Sleep time (24:00)"), 0, 8);
        TextField sleepField = new TextField();
        recordPanel.add(sleepField, 1, 8);

        recordPanel.add(new Label("Wake time (24:00)"), 0, 9);
        TextField wakeTime = new TextField();
        recordPanel.add(wakeTime, 1, 9);

        Button submitButton = getSubmitButton(recordPanel);
        recordPanel.add(submitButton, 1,10);

        borderPane.setCenter(recordPanel);
    }

    private Button getSubmitButton(GridPane recordPanel) {
        Button submitButton = new Button("Submit!");
        submitButton.setMinWidth(100);

        submitButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                int count = 1;
                double[] res = new double[11];
                for (Node n : recordPanel.getChildren()) {
                    if (!(n instanceof TextField)){
                        continue;
                    }
                    // TODO: merge with num validator (pretty easy change but not too important rn)
                    res[count] = Double.parseDouble(((TextField) n).getText());
                    count++;
                }

                if (user == null) {
                    user = new User(new ArrayList<>());
                    user.addRow(res);
                }
            }
        });
        return submitButton;
    }

}

