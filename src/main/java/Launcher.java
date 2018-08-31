import GUI.Configurator;
import GUI.Data;
import GUI.LauncherUI;

/**
 * Launches the UI.
 */
public class Launcher
{
    public static void main(String[] args)
    {
       // Read properties, will create an emptied properties file if none exist.
        Data.getINSTANCE().readProperties();

        // Check for .path file
        if (checkForIni(args))
            return;


        // Check if Paths are still valid / not changed. If they have changed run configurator again.
        if (checkDirs(args))
            return;

        // Run the launcher.
        LauncherUI launcherUI = new LauncherUI();
        launcherUI.ui_launch(args);
    }

    private static boolean checkForIni(String[] args)
    {
        if (Data.getINSTANCE().getAddon_path().equals("") || Data.getINSTANCE().getProgram_path().equals(""))
        {
            // Run Path setter.
            Configurator configurator = new Configurator();
            configurator.ui_launch(args);
            return true;
        }
        return false;
    }

    private static boolean checkDirs(String[] args)
    {
        if (!Data.getINSTANCE().checkDirs())
        {
            Configurator configurator = new Configurator();
            configurator.ui_launch(args);
            return true;
        }
        return false;
    }
}
