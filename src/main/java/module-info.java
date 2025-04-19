module SleepTracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens SleepTracker to javafx.fxml;
    exports SleepTracker;
}