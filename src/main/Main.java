package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/icon_app.png"))));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Hospital Managemnet System");
        primaryStage.setWidth(1270);
        primaryStage.setHeight(750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}