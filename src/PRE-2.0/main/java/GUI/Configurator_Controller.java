package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for the Configurator.
 */
@Deprecated
public class Configurator_Controller
{
    @FXML
    private AnchorPane step1;

    @FXML
    private AnchorPane step2;

    @FXML
    private TextField addon_text;

    @FXML
    private TextField text_versions;

    @FXML
    private AnchorPane step3;

    @FXML
    private TextField steam_path;

    @FXML
    private TextField path_standalone;

    @FXML
    private TextField path_beta;

    @FXML
    private AnchorPane step4;

    @FXML
    private TextArea txtDump;

    /**
     * Simple method for asking what the currently visible anchor pane is.
     * @return
     */
    private int visibleAnchorPane()
    {
        if (step1.isVisible())
         return 1;
        else if (step2.isVisible())
         return 2;
        else if (step3.isVisible())
         return 3;
        return 4;
    }

    /**
     * Handles what the next button does.
     */
    public void ButtonNext() throws Exception
    {
        int vis = visibleAnchorPane();

        switch (vis)
        {
            case 1 :
                Platform.runLater(() -> step1()); break;
            case 2 : step2(); break;
            case 3 : step3(); break;
            case 4 : step4(); break;
        }
    }

    /**
     * Shows step2 and hides step1.
     */
    public void step1()
    {
        step1.setVisible(false);
        step2.setVisible(true);
    }

    /**
     * Hides step 2, and shows step 3.
     * IF the path locations of Addons and Versions have been set by the user.
     */
    public void step2()
    {
        String addon_location = addon_text.getText();
        String version_location = text_versions.getText();
        boolean one_not_filled = false;

        File checker = new File(addon_location);
        if (!checker.exists())
        {
            updateText(addon_text, "This field must be filled in!");
            one_not_filled = true;
        }
        checker = new File(version_location);
        if (!checker.exists())
        {
            updateText(text_versions, "This field must be filled in!");
            one_not_filled = true;
        }

        if (one_not_filled)
            return;

        step2.setVisible(false);
        step3.setVisible(true);
    }

    /**
     * Helper method because I grew tired of typing this line.
     * It updates the text in a textfield.
     */
    public void updateText(TextField t, String s)
    {
        Platform.runLater(() -> t.setText(s));
    }

    /**
     * Hides step 3 and shows Step 4.
     * IF a user has provided at least one Path for Path of Exile.
     */
    public void step3()
    {
        ArrayList<TextField> filled_in = new ArrayList<>();
        String p_steam = steam_path.getText();
        String p_standalone = path_standalone.getText();
        String p_beta = path_beta.getText();

        int init = 0;

        if (checkIfFilledIn(p_steam))
        {
            init++;
            filled_in.add(steam_path);
        }
        if (checkIfFilledIn(p_standalone))
        {
            init++;
            filled_in.add(path_standalone);
        }
        if (checkIfFilledIn(p_beta))
        {
            init++;
            filled_in.add(path_beta);
        }

        if (init < 1)
            return;

        boolean stopper = false;

        for (TextField field : filled_in)
        {
            File f = new File(field.getText());
            if (!f.exists())
            {
                updateText(field, "ERROR, TRY AGAIN");
                stopper = true;
            }
        }

        if (stopper)
            return;

        // Finally check for a runnable .exe
        for (TextField field : filled_in)
        {
            if (!dirChecker(Objects.requireNonNull(new File(field.getText()).listFiles())))
            {
                updateText(field, "No PoE Executable was found.");
                stopper = true;
            }
        }

        if (stopper)
            return;

        Platform.runLater(() -> txtDump.setText(textDump()));

        step3.setVisible(false);
        step4.setVisible(true);
    }

    /**
     * Checks a Dir for being a valid Path of Exile installation Folder.
     */
    public boolean dirChecker(File[] listOfFiles)
    {
        for (File f : listOfFiles)
        {
            String name = f.getName();
            switch (name)
            {
                case "Client.exe":
                    return true;
                case "PathOfExile.exe":
                    return true;
                case "PathOfExileSteam.exe":
                    return true;
                case "PathOfExile_x64.exe":
                    return true;
                case "PathOfExile_x64Steam.exe":
                    return true;
            }
        }
        return false;
    }

    /**
     * Method for generating the text dump during Step 4.
     */
    public String textDump()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This data will be stored:\n\nAddons Folder:\n");
        stringBuilder.append(addon_text.getText());
        stringBuilder.append("\n\nVersions Folder:\n");
        stringBuilder.append(text_versions.getText());
        stringBuilder.append("\n\nPath of Exile Folder(s):\nSteam->");
        stringBuilder.append(steam_path.getText());
        stringBuilder.append("\nStand Alone->");
        stringBuilder.append(path_standalone.getText());
        stringBuilder.append("\nBETA Client->");
        stringBuilder.append(path_beta.getText());
        return stringBuilder.toString();
    }

    /**
     * Helper method for Step 3.
     */
    public boolean checkIfFilledIn(String string)
    {
        return !string.equals("") && !string.equals("ERROR, TRY AGAIN") && !string.equals("No PoE Executable was found.");
    }

    /**
     * Commits the Paths to a .path file.
     * Hides the Configurator and launches the LauncherUI.
     */
    public void step4() throws Exception
    {
        // Save Settings
        Data.getINSTANCE().setAddon_path(addon_text.getText());
        Data.getINSTANCE().setProgram_path(text_versions.getText());
        Data.getINSTANCE().setBeta_path(path_beta.getText());
        Data.getINSTANCE().setStand_alone(path_standalone.getText());
        Data.getINSTANCE().setSteam_path(steam_path.getText());

        Data.getINSTANCE().createProperties();

        Configurator.stage.close();
        LauncherUI launcherUI = new LauncherUI();
        launcherUI.start(new Stage());
    }

    /**
     * Opens the Browser, method defined in Path.fxml
     */
    public void browseAddonFolder()
    {
        File f = browse("Browse for a folder to download addons to:");
        if (f==null)
            return;
        Platform.runLater(() -> addon_text.setText(f.getPath()));
    }

    /**
     * Opens the Browser, method defined in Path.fxml
     */
    public void browseVersionFolder()
    {
        File f = browse("Browse for a folder to install PAL to:");
        if (f==null)
            return;
        Platform.runLater(() -> text_versions.setText(f.getPath()));
    }

    /**
     * Opens the Browser, method defined in Path.fxml
     */
    public void browseBetaVersion()
    {
        File f = browse("Browse for your steam version of Path of Exile");
        if (f==null)
            return;
        Platform.runLater(() -> path_beta.setText(f.getPath()));
    }

    /**
     * Opens the Browser, method defined in Path.fxml
     */
    public void browseStandAloneVersion()
    {
        File f = browse("Browse for your stand-alone version of Path of Exile");
        if (f==null)
            return;
        Platform.runLater(() -> path_standalone.setText(f.getPath()));
    }

    /**
     * Opens the Browser, method defined in Path.fxml
     */
    public void browseSteamFolder()
    {
        File f = browse("Browse for a beta version of Path of Exile");
        if (f==null)
            return;
        Platform.runLater(() -> steam_path.setText(f.getPath()));
    }

    /**
     * Method for Opening the DirectoryChooser.
     */
    public File browse(String title)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(Configurator.stage);
    }


}

