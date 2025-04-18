package SleepTracker;

import javafx.fxml.FXML;
import javafx.event.*;
import javafx.fxml.*;
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
    void quit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void initialize() {
        screen_output.setEditable(false);
    }
}

