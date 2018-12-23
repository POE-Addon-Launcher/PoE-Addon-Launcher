package GUI.PopUp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class PopUp_Controller implements Initializable
{

    @FXML
    private Text bold_text;

    @FXML
    private ImageView topbar_image_closeWindow;

    @FXML
    private Text topbar_text;

    @FXML
    private ChoiceBox<File> comboFiles;

    @FXML
    private Text topbar_text1;

    @FXML
    private Text topbar_text11;

    @FXML
    private Text topbar_text111;

    @FXML
    private Button bOpenBrowser;

    @FXML
    private TextField fieldDL_Link;

    @FXML
    private Text topbar_text1111;

    @FXML
    private Button bContinue;

    @FXML
    private RadioButton rDL;

    @FXML
    private RadioButton rLocal;

    @FXML
    void continue_program(ActionEvent event)
    {
        if (rLocal.isSelected())
        {
            File f = comboFiles.getSelectionModel().getSelectedItem();
            if (f != null)
            {
                if (f.exists())
                {
                    Popup.jar = f;
                }
            }
        }
        else
        {
            // Check DL link.
            String str = fieldDL_Link.getText();
            if (str.matches("(https://github.com/POE-Addon-Launcher/Core/releases/download/b)(\\d){1,9999999}(/b)(\\d){1,9999999}(.jar)"))
            {
                Popup.url = str;
            }
            else
            {
                Platform.runLater(() -> topbar_text1111.setText("ERROR: Your download link is not valid!"));
            }
        }
    }


    @FXML
    void openBrowser()
    {
        if (Desktop.isDesktopSupported())
            {
                try
                {
                    Desktop.getDesktop().browse(new URI("https://github.com/POE-Addon-Launcher/Core/releases"));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Platform.runLater(() -> topbar_text1111.setText("Can't open browser, please go to https://github.com/POE-Addon-Launcher/Core/releases"));

                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                    Platform.runLater(() -> topbar_text1111.setText("Can't open browser, please go to https://github.com/POE-Addon-Launcher/Core/releases"));
                }
            }
            else
            {
                Platform.runLater(() -> topbar_text1111.setText("Can't open browser, please go to https://github.com/POE-Addon-Launcher/Core/releases"));
            }
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void onMouseDragged(MouseEvent mouseEvent)
    {
        Popup.stage.setX(mouseEvent.getScreenX() + xOffset);
        Popup.stage.setY(mouseEvent.getScreenY() + yOffset);
    }

    @FXML
    public void onMousePressed(MouseEvent mouseEvent)
    {
        xOffset = Popup.stage.getX() - mouseEvent.getScreenX();
        yOffset = Popup.stage.getY() - mouseEvent.getScreenY();
    }

    @FXML
    public void topbar_closeWIndow_onMouseClicked()
    {
        Popup.stage.close();
        Popup.jar = Popup.files.get(Popup.files.size()-1);
    }

    @FXML
    public void topbar_closeWindow_onMouseEntered()
    {
        changeImage(topbar_image_closeWindow, "/cancel.png");
    }

    @FXML
    public void topbar_closeWindow_onMouseExited()
    {
        changeImage(topbar_image_closeWindow, "/cancel0.png");
    }

    private void changeImage(ImageView imageView, String s)
    {
        Platform.runLater(() -> imageView.setImage(new Image(getClass().getResource(s).toString())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> comboFiles.setItems(Popup.files));
        daemon_radio();
    }

    public void daemon_radio()
    {
        Runnable r = () ->
        {
            while (true)
            {
                if (fieldDL_Link.isFocused() && !rDL.isSelected())
                {
                    rDL.fire();
                }
                else if (comboFiles.isFocused() && !rLocal.isSelected())
                {
                    rLocal.fire();
                }
                try
                {
                    Thread.sleep(300L);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
}
