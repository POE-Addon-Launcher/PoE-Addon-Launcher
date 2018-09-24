package GUI;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Data Class.
 * *gasp* That's not proper coding!
 * Yes, but it is so much easier than doing it proper :).
 */
@Deprecated
public final class Data
{
    private static final Data INSTANCE = new Data();
    private static double progress = 0;
    private static String status = "";
    private String addon_path = "";
    private String program_path = "";
    private String steam_path = "";
    private String stand_alone = "";
    private String beta_path = "";
    private String jar_filepath = "";

    private Data()
    {}

    public static Data getINSTANCE()
    {
        return INSTANCE;
    }

    public static double getProgress()
    {
        return progress;
    }

    public static void setProgress(double progress)
    {
        Data.progress = progress;
    }

    public static String getStatus()
    {
        return status;
    }

    public static void setStatus(String status)
    {
        System.out.println(status);
        Data.status = status;
    }

    /**
     * Create properies.
     */
    public void createProperties()
    {
        Properties poe_paths = new Properties();
        OutputStream out = null;
        OutputStream secondary = null;

        File f = new File(program_path + "/.settings");

        try
        {
            out = new FileOutputStream(".path");
            poe_paths.setProperty("addon_folder", addon_path);
            poe_paths.setProperty("version_folder", program_path);
            poe_paths.setProperty("steam", steam_path);
            poe_paths.setProperty("standalone", stand_alone);
            poe_paths.setProperty("beta", beta_path);
            poe_paths.store(out, "Only change this if you know what you are doing!");

            if (!f.exists())
            {
                secondary = new FileOutputStream(f.getPath());
                poe_paths.store(secondary, "Only change this if you know what you are doing!");
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            if (secondary != null)
            {
                try
                {
                    secondary.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a list of Dirs.
     */
    public ArrayList<String> createListOfDirs(String... args)
    {
        ArrayList<String> check_locations = new ArrayList<>();
        for (String s : args)
        {
            if (!s.equals(""))
            {
                check_locations.add(s);
            }
        }
        return check_locations;
    }

    /**
     * Checks if we have more than 2 Dirs in the .path file.
     * (Final safety check)
     */
    public boolean checkDirs()
    {
        ArrayList<String> dirs = createListOfDirs(addon_path, program_path, steam_path, stand_alone, beta_path);

        // Versions & Addons are set, but no game path.
        if (dirs.size() <= 2)
            return false;

        for (String s : dirs)
        {
            File f = new File(s);
            // One of the paths no longer exists.
            if (!f.exists())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Reads from the .path properties file.
     */
    public void readProperties()
    {
        if (!checkProperties())
        {
            createProperties();
        }
        Properties poe_paths = new Properties();
        InputStream in = null;

        try
        {
            in = new FileInputStream(".path");
            poe_paths.load(in);
            addon_path = poe_paths.getProperty("addon_folder");
            program_path = poe_paths.getProperty("version_folder");
            steam_path = poe_paths.getProperty("steam");
            stand_alone = poe_paths.getProperty("standalone");
            beta_path = poe_paths.getProperty("beta");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if the .path properties file exists.
     */
    public boolean checkProperties()
    {
        File f = new File(".path");
        return f.exists();
    }

    public String getAddon_path()
    {
        return addon_path;
    }

    public void setAddon_path(String addon_path)
    {
        this.addon_path = addon_path;
    }

    public String getProgram_path()
    {
        return program_path;
    }

    public void setProgram_path(String program_path)
    {
        this.program_path = program_path;
    }

    public String getSteam_path()
    {
        return steam_path;
    }

    public void setSteam_path(String steam_path)
    {
        this.steam_path = steam_path;
    }

    public String getStand_alone()
    {
        return stand_alone;
    }

    public void setStand_alone(String stand_alone)
    {
        this.stand_alone = stand_alone;
    }

    public String getBeta_path()
    {
        return beta_path;
    }

    public void setBeta_path(String beta_path)
    {
        this.beta_path = beta_path;
    }

    public String getJar_filepath()
    {
        return jar_filepath;
    }

    public void setJar_filepath(String jar_filepath)
    {
        this.jar_filepath = jar_filepath;
    }
}
