package SleepTracker;

import com.sun.tools.javac.Main;
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
        TextField wakeTimeField = new TextField();
        recordPanel.add(wakeTimeField, 1, 9);

        Button submitButton = getSubmitButton(recordPanel);
        recordPanel.add(submitButton, 1,10);

        borderPane.setCenter(recordPanel);
    }

    @FXML
    void AnalyzeScreen(ActionEvent event) {
        borderPane.setCenter(null);
        GridPane analyze_options = new GridPane();
        analyze_options.setHgap(10);
        analyze_options.setVgap(10);
        analyze_options.setPadding(new Insets(20));
        //Options are listed below
        analyze_options.add(new Label("Show recorded data so far: "), 0, 0);
        Button Show_data = new Button();
        Show_data.setText("Display data");
        analyze_options.add(Show_data, 1, 0);

        analyze_options.add(new Label("Show factors affecting sleep quality: "), 0, 1);
        Button Show_factors = new Button();
        Show_factors.setText("Display factors");
        analyze_options.add(Show_factors, 1, 1);

        analyze_options.add(new Label("Show factors affecting sleep duration: "), 0, 2);
        Button Show_sleep = new Button();
        Show_sleep.setText("Display sleep Duration");
        analyze_options.add(Show_sleep, 1, 2);

        analyze_options.add(new Label("Show averages of recorded data: "), 0, 3);
        Button Show_averages = new Button();
        Show_averages.setText("Display averages");
        analyze_options.add(Show_averages, 1, 3);

        analyze_options.add(new Label("Show relative consistency of sleep/wake times: "), 0, 4);
        Button Show_relative_consistency = new Button();
        Show_relative_consistency.setText("Display relative consistency");
        analyze_options.add(Show_relative_consistency, 1, 4);

        analyze_options.add(new Label("Show last night's # of sleep cycles: "), 0, 5);
        Button Show_last_night = new Button();
        Show_last_night.setText("Display sleep cycles");
        analyze_options.add(Show_last_night, 1, 5);

        analyze_options.add(new Label("Show last night's REM duration: "), 0, 6);
        Button Show_REM = new Button();
        Show_REM.setText("Display REM");
        analyze_options.add(Show_REM, 1, 6);

        analyze_options.add(new Label("Show SD of sleep duration: "), 0, 7);
        Button Show_SD = new Button();
        Show_SD.setText("Display SD");
        analyze_options.add(Show_SD, 1, 7);

        analyze_options.add(new Label("Exit: "), 0, 8);
        Button Exit = new Button();
        Exit.setText("Exit");
        analyze_options.add(Exit, 1, 8);

        borderPane.setCenter(analyze_options);
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

    @FXML
    void save() {
        MainShell.save(user.getUserData());
        // TODO: should show status
    }

    @FXML
    void saveAs() {
        FileChooser fc = new FileChooser();
        try {
            MainShell.save(user.getUserData(), fc.showSaveDialog(new Stage()).getPath());
        } catch (Exception e) {
            // TODO: should show status
        }
    }


    @FXML
    void load() {
        try {
            user = MainShell.load();
        } catch (Exception _) {
            // TODO: should also show status
        }
    }

    @FXML
    void loadFrom() {
        try {
            FileChooser fc = new FileChooser();
            user = MainShell.load(fc.showSaveDialog(new Stage()).getPath());
        } catch (Exception _) {
            // TODO: should also show status
        }
    }
}

