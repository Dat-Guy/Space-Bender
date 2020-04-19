package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene primaryScene = new Scene(root, 600, 550);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
