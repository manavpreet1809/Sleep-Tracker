module SleepTracker {
    requires javafx.controls;
    requires javafx.fxml;


    opens SleepTracker to javafx.fxml;
    exports SleepTracker;
}