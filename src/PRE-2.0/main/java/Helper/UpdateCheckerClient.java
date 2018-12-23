package Helper;

import GUI.Data;
import GUI.Data2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Class handles github API requests.
 */
public final class UpdateCheckerClient
{
    private static final UpdateCheckerClient INSTANCE = new UpdateCheckerClient();
    private final String RELEASE_URL = "https://api.github.com/repos/POE-Addon-Launcher/Core/releases";

    private UpdateCheckerClient()
    {}

    public static UpdateCheckerClient getINSTANCE()
    {
        return INSTANCE;
    }

    /**
     * Downloads an update from the github servers and puts it in the Versions folder.
     * Creates a folder aswell.
     * @param most_recent The most recent release that has to be downloaded.
     */
    @Deprecated
    public void downloadUpdate(Release most_recent)
    {
        File versions_dir = new File(Data2.getINSTANCE().getCoreFolder());

        if (versions_dir.isDirectory())
        {
            File[] directories = versions_dir.listFiles();
            File mr_dir = new File(Data2.getINSTANCE().getCoreFolder());
            for (File dir : directories)
            {
                if (dir.getName().equals(most_recent.getName()))
                {
                    //PAL.getINSTANCE().setJar_filepath(mr_dir.toString() + "\\" + most_recent.getName());
                    Data2.getINSTANCE().setJar_name_to_launch(Data2.getINSTANCE().getCoreFolder() + File.separator + most_recent.getName());
                    Data.setStatus("Up to date, launching program!");
                    return;
                }
            }
            // Create DIR
            try
            {
                InputStream in = URI.create(most_recent.getDownload_url()).toURL().openStream();
                Files.copy(in, Paths.get(mr_dir.toString() + "/" + most_recent.getName()));
                Data.setStatus("Downloaded: " + most_recent.getName());
                Data2.getINSTANCE().setJar_name_to_launch(Data2.getINSTANCE().getCoreFolder() + File.separator + most_recent.getName());
                //PAL.getINSTANCE().setJar_filepath(mr_dir.toString() + "\\" + most_recent.getName());
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * Downloads an update from the github servers and puts it in the Versions folder.
     * Creates a folder aswell.
     * @return returns a String to be executed.
     */
    public String downloadUpdate(String URL)
    {
        File core_dir = new File(Data2.getINSTANCE().getCoreFolder());
        String[] parts = URL.split("/");
        String jarname = parts[parts.length-1];

        a:if (!jarname.matches("(b)(\\d){1,9999999}(.jar)"))
        {
            jarname = null;
            // Attempt to get it through bruteforce.
            for (String str : parts)
            {
                if (str.matches("(b)(\\d){1,9999999}(.jar)"))
                {
                    jarname = str;
                    break;
                }
            }
        }

        File file = new File(core_dir.getPath() + File.separator + jarname);

        if (file.exists())
        {
            return file.getPath();
        }

        if (jarname != null)
        {
            if (core_dir.isDirectory())
            {
                try
                {
                    InputStream in = URI.create(URL).toURL().openStream();
                    Files.copy(in, file.toPath());
                    return file.getPath();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return "NO_DOWNLOAD";
    }

    /**
     * Genereates an ArrayList of releases.
     * @return Returns an ArrayList of releases.
     */
    @Deprecated
    public ArrayList<Release> getReleases()
    {
        if (!canCheckGithub())
        {
            return null;
        }


        ArrayList<Release> releases = new ArrayList<>();
        try
        {
            URL url = new URL(RELEASE_URL);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(url);
            // Collection of all releases.
            Iterator<JsonNode> elements = node.elements();
            for (Iterator<JsonNode> it = elements; it.hasNext(); )
            {
                JsonNode n = it.next();
                Release r = new Release(n);
                releases.add(r);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }  catch (IOException e)
        {
            e.printStackTrace();
        }
        return releases;
    }

    @Deprecated
    private boolean canCheckGithub()
    {
        try
        {
            GitHub github = GitHub.connectAnonymously();
            return github.getRateLimit().remaining > 0;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
