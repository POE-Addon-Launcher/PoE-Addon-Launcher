package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Just a JavaFX class, nothing special.
 */
public class Configurator extends Application
{
    public static Stage stage;

    public void start(Stage primaryStage) throws Exception
    {
        primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/Configurator2.fxml"));
        primaryStage.setTitle("PAL: Configurator");
        primaryStage.getIcons().add(new Image(getClass().getResource("/witch.png").toString()));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 308, 200));
        Configurator.stage = primaryStage;
        primaryStage.show();
    }

    public void ui_launch(String[] args)
    {
        launch(args);
    }
}
