package GUI;

import Helper.QuickSorter;
import Helper.Release;
import Helper.UpdateCheckerClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * LauncherUI_Controller does the following:
 *   Shows a graphical splash screen that shows some information.
 *   Creates a list of @{Release} from the github API.
 *   Checks the versions folder (pre-selected by user) for the available versions.
 *   Downloads a new version if one is available.
 *   Launches the the most recent jar.
 */
public class LauncherUI_Controller implements Initializable
{
    @FXML
    private Text status_text;

    /**
     * Initialize the daemons.
     */
    public void initialize(URL location, ResourceBundle resources)
    {
        daemon_progress();
        daemon_checker();
        daemon_launch();
    }

    /**
     * This daemon will terminate the launcher and launch the core program.
     */
    private void daemon_launch()
    {
        Runnable r = () ->
        {
            while (true)
            {
                try
                {
                    Thread.sleep(300L);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if (Data.getProgress() == 1.0d)
                {
                    // Launch and close current UI.

                    Runtime runtime = Runtime.getRuntime();
                    try
                    {
                        runtime.exec("java -jar " + Data.getINSTANCE().getJar_filepath());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Stage s = LauncherUI.stage;
                    Platform.runLater(() -> s.close());
                    break;
                }
            }
        };
        Thread t_launch = new Thread(r);
        t_launch.setDaemon(true);
        t_launch.start();
    }

    /**
     * Core checking thread.
     */
    private void daemon_checker()
    {
        Runnable r = () ->
        {
            Data.setProgress(0.0);
            Data.getINSTANCE().createProperties();

            // Create Releases Array
            Data.setStatus("Accessing github for releases");
            ArrayList<Release> releases = UpdateCheckerClient.getINSTANCE().getReleases();

            Data.setStatus("Got all " + releases.size() + " releases!");

            Data.setStatus("Checking if we're up to date");
            // Launch Checker
            UpdateCheckerClient.getINSTANCE().downloadUpdate(getMostRecentRelease(releases));

            Data.setStatus("Up to date, launching program!");
            Data.setProgress(1.0);
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Retrieves the most recent release from an ArrayList.
     * (*Probably shouldn't be in this class)
     */
    private Release getMostRecentRelease(ArrayList<Release> releases)
    {
        QuickSorter quickSorter = new QuickSorter();

        Release[] array = new Release[releases.size()];

        for (int c = 0; c < releases.size(); c++)
        {
            if (releases.get(c) != null)
            {
                array[c] = releases.get(c);
            }
        }
        // Sort by Highest Number (latest release)
        quickSorter.sort(array);
        return array[0];
    }

    /**
     * Handles updating the progressbar and the status text.
     */
    private void daemon_progress()
    {
        Runnable r = () ->
        {
            while (true)
            {
                if (!status_text.getText().equals(Data.getStatus()))
                {
                    Platform.runLater(() -> status_text.setText(Data.getStatus()));
                }
                try
                {
                    Thread.sleep(300L);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            // Launch Main UI.
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
}
