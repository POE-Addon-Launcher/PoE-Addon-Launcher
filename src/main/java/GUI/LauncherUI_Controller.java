package GUI;

import API.GithubHandler;
import GUI.PopUp.Popup;
import Helper.QuickSorter;
import Helper.Release;
import Helper.UpdateCheckerClient;
import PAL.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
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
        cleanUp();
        daemon_progress();
        daemon_checker();
        daemon_launch();
    }

    private boolean isNewer(String _old, String _new)
    {
        _old = _old.replace("b", "");
        _old = _old.replace(".jar", "");
        _new = _new.replace("b", "");
        _new =_new.replace(".jar", "");

        int num_old = Integer.parseInt(_old);
        int num_new = Integer.parseInt(_new);

        return num_new > num_old;
    }

    private File mr_jar;
    private void cleanUp()
    {
        File f = new File(Data2.getINSTANCE().getCoreFolder());
        if (f.exists())
        {
            if (f.isDirectory())
            {
                File[] files = f.listFiles();
                File most_recent_jar = new File("b0.jar");
                for (File file : files)
                {
                    if (file.getName().contains(".jar"))
                    {
                        if (isNewer(most_recent_jar.getName(), file.getName()))
                        {
                            most_recent_jar = file;
                        }
                    }
                }
                mr_jar = most_recent_jar;
                for (File file : files)
                {
                    if (file.getName().contains(".jar"))
                    {
                        if (!file.getName().equals(most_recent_jar.getName()))
                        {
                            file.delete();
                        }
                    }
                }
            }
        }
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
                        runtime.exec("java -jar " + Data2.getINSTANCE().getJar_name_to_launch());
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

    private String github_token = null;

    public boolean readCoreSettings()
    {
        File core_settings_pal = new File(Data2.getINSTANCE().getLocal() + File.separator + "PAL" + File.separator + "core_settings.pal");

        PALsettings palsettings = new PALsettings(false, "", true, "", true, false, "", "", false, false);

        if (core_settings_pal.exists())
        {
            ObjectMapper objectMapper = new ObjectMapper();
            try
            {
                palsettings = objectMapper.readValue(core_settings_pal, PALsettings.class);
                github_token = palsettings.getGithub_token();
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void noAPIrequests()
    {
        File f = new File(Data2.getINSTANCE().getCoreFolder());
        if (f.exists())
        {
            if (f.isDirectory())
            {
                Popup popup = new Popup();
                for (File f_ : f.listFiles())
                {
                    if (f_.getName().matches("b(\\d){0,9999999}(.jar)"))
                    {
                        Popup.files.add(f_);
                    }
                }

                Platform.runLater(() ->
                {
                    try
                    {
                        popup.start(new Stage());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });
                final String wait = "Waiting for user to select an option...";
                while (true)
                {
                    if (!Data.getStatus().equals(wait))
                        Data.setStatus(wait);

                    try
                    {
                        Thread.sleep(300L);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if (Popup.jar != null)
                    {
                        Data.setStatus("Attempting to launch: " + Popup.jar.getName());
                        Platform.runLater(() -> Popup.stage.close());
                        runSpecificVersion(Popup.jar);
                        break;
                    }

                    if (!Popup.url.equals("NOTSET"))
                    {
                        Platform.runLater(() -> Popup.stage.close());
                        Data.setStatus("Attempting to download from: " + Popup.url);
                        // Download and run.
                        String jar = UpdateCheckerClient.getINSTANCE().downloadUpdate(Popup.url);
                        if (jar.equals("NO_DOWNLOAD"))
                        {
                            Platform.runLater(() ->
                            {
                                try
                                {
                                    popup.start(new Stage());
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            });
                        }
                        else
                        {
                            runSpecificVersion(new File(jar));
                            break;
                        }
                    }
                }
            }
            else
            {
                System.out.println("Core folder is not a directory?");
            }
        }
        else
        {
            System.out.println("Core folder doesn't exist!");
        }
    }

    /**
     * Core checking thread.
     */
    private void daemon_checker()
    {
        Runnable r = () ->
        {
            Data.setProgress(0.0);

            // Check if core_settings.pal exists and get Github API Token.
            Data.setStatus("Attempting to read core_settings.pal");
            readCoreSettings();

            boolean canConnect;
            GitHub gitHub = GithubHandler.connect(github_token);
            assert gitHub != null;
            canConnect = GithubHandler.canQuerry(gitHub);

            if (canConnect)
            {
                try
                {
                    GHRepository repository = gitHub.getRepository("POE-Addon-Launcher/Core");

                    String current_most_recent = repository.getLatestRelease().getTagName() + ".jar";

                    File f = new File(Data2.getINSTANCE().getCoreFolder());

                    for (File f_ : f.listFiles())
                    {
                        if (f_.getName().equals(current_most_recent))
                        {
                            Data.setStatus("You have " + current_most_recent + " which is the most recent build!");
                            runSpecificVersion(f_);
                            return;
                        }
                    }
                    Data.setStatus("You are out of date! Downloading new version...");
                    String jar = UpdateCheckerClient.getINSTANCE().downloadUpdate(repository.getLatestRelease().getAssets().get(0).getBrowserDownloadUrl());
                    runSpecificVersion(new File(jar));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Data.setStatus("You have no GitHub API requests left; Get yourself a GitHub API Token!");
                noAPIrequests();
            }


            //PAL.getINSTANCE().createProperties();
            /*
            // Create Releases Array
            Data.setStatus("Accessing github for releases");
            ArrayList<Release> releases = UpdateCheckerClient.getINSTANCE().getReleases();
            if (releases == null)
            {
                // Check dir for files.

            }
            else
            {
                Data.setStatus("Got all " + releases.size() + " releases!");

                Data.setStatus("Checking if we're up to date");
                // Launch Checker
                UpdateCheckerClient.getINSTANCE().downloadUpdate(getMostRecentRelease(releases));
            }
            */
            Data.setStatus("Up to date, launching program!");
            Data.setProgress(1.0);
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    private void runSpecificVersion(File file)
    {
        Runtime runtime = Runtime.getRuntime();
        try
        {
            runtime.exec("java -jar " + "\"" + file.getPath() + "\"");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Stage s = LauncherUI.stage;
        Platform.runLater(() -> s.close());

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
        System.out.println(array[0].toString());
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
