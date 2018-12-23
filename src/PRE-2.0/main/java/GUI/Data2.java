package GUI;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Properties;

/**
 * .data
 */
public final class Data2
{
    private static final Data2 INSTANCE = new Data2();

    private Data2()
    {}

    public static Data2 getINSTANCE()
    {
        return INSTANCE;
    }

    private ObservableList<String> poe_paths;
    private launcher_data data;
    private String local = System.getenv("LOCALAPPDATA");
    private String jar_name_to_launch;

    public void savePoEpaths()
    {
        File f = new File(local + File.separator + "PAL" + File.separator + "poe_paths.pal");
        if (f.exists())
        {
            f.delete();
        }

        String[] array = new String[poe_paths.size()];
        for (int c = 0; c < array.length; c++)
        {
            array[c] = poe_paths.get(c);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        if (array.length > 0)
        {
            try
            {
                objectMapper.writeValue(f, array);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveLauncherData()
    {
        File f = new File(local + File.separator + "PAL" + File.separator + "launcher_data.pal");
        if (f.exists())
        {
            f.delete();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            objectMapper.writeValue(f, data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Read launcher data.
     */
    public boolean readLauncherData()
    {
        String local = System.getenv("LOCALAPPDATA");
        File f = new File(local + File.separator + "PAL" + File.separator + "launcher_data.pal");
        if (f.exists())
        {
            ObjectMapper objectMapper = new ObjectMapper();
            try
            {
                data = objectMapper.readValue(f, launcher_data.class);
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public ObservableList<String> getPoe_paths()
    {
        return poe_paths;
    }

    public void setPoe_paths(ObservableList<String> poe_paths)
    {
        this.poe_paths = poe_paths;
    }

    public launcher_data getData()
    {
        return data;
    }

    public void setData(launcher_data data)
    {
        this.data = data;
    }

    public String getLocal()
    {
        return local;
    }

    public void setLocal(String local)
    {
        this.local = local;
    }

    public String getCoreFolder()
    {
        return data.getInstall_dir() + File.separator + "Core";
    }

    public String getAddonFolder()
    {
        return data.getInstall_dir() + File.separator + "Addons";
    }

    public void createFolders()
    {
        createFolder("Addons");
        createFolder("Core");
        createFolder("temp_downloads");
    }

    public void createFolder(String name)
    {
        File file = new File(data.getInstall_dir() + File.separator + name);
        if (file.exists())
        {
            System.err.println("Trying to create " + name + "folder but it already exists this shouldn't happen!");

        }
        else
        {
            boolean mkdir = file.mkdir();
            if (!mkdir)
            {
                System.err.println("Can't make directory: " + file.getPath());
            }
        }
    }

    public void createPALfolder()
    {
        File pal_folder = new File(local + File.separator + "PAL");
        if (!pal_folder.exists())
        {
            pal_folder.mkdir();
        }
    }

    public String getJar_name_to_launch()
    {
        return jar_name_to_launch;
    }

    public void setJar_name_to_launch(String jar_name_to_launch)
    {
        this.jar_name_to_launch = jar_name_to_launch;
    }
}
