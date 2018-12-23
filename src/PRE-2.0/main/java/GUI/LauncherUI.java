package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Just a JavaFX class, nothing special.
 */
public class LauncherUI extends Application
{
    public static Stage stage;

    public void start(Stage primaryStage) throws Exception
    {
        primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = fxmlLoader.load(getClass().getResource("/Launcher.fxml"));
        primaryStage.setTitle("PAL: Launcher");
        primaryStage.getIcons().add(new Image(getClass().getResource("/witch.png").toString()));
        primaryStage.setScene(new Scene(root, 400, 250));
        stage = primaryStage;
        primaryStage.show();
    }

    public void ui_launch(String[] args)
    {
        launch(args);
    }
}
