# 233 Final

by Dylan, Gary, and Manav.
## What is it?
This is an application designed to help you track your sleep! Record information about your sleep, 
and see helpful and interesting information, such as factors which influence your sleep quality, consistency over time, and more.

## Get started
### Windows installation instructions:
1. Make sure you have the [JDK](https://www.oracle.com/java/technologies/downloads/) installed.
2. Install JavaFX from https://gluonhq.com/products/javafx/.
3. Unzip the file, and copy the folder that looks like javafx-sdk-XX.X.X.
4. Paste that folder into C:\Program Files\Java\\.
5. You now have two options: either run 233-final.jar with this command:`java --module-path "C:\Program Files\Java\javafx-sdk-XX.X.X\lib" --add-modules
   javafx.controls,javafx.fxml -jar CPSC233W25A3.jar`
   or navigate to 233-final/src/main/java/SleepTracker/ and run 
   `java --module-path "C:\Program Files\Java\javafx-sdk-XX.X.X\lib" --add-modules javafx.controls,javafx.fxml SleepTrackerMainController.java` 
(Replace XX.X.X with your JavaFX version.) 