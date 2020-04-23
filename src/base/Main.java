package base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    /*TODO: Begin introducing ships (begin with player/AI controlled asteroids, then give them properties based on
    composition, and finally allow for internal logic/player character building instead of "click to add parts"
    For identification of different parts, should create color chart.
    Might begin rudimentary graphics in order to make visual recognition and distinction between objects easier (asteroids
    are grey, ships are white with colored parts (like blue thrusters, red blasters, etc), etc)
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = FXMLLoader.load(getClass().getResource("base.fxml"));
        Scene primaryScene = new Scene(root, 600, 550);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
