package Helper;

import GUI.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void downloadUpdate(Release most_recent)
    {
        File versions_dir = new File(Data.getINSTANCE().getProgram_path());

        if (versions_dir.isDirectory())
        {
            File[] directories = versions_dir.listFiles();
            File mr_dir = new File(Data.getINSTANCE().getProgram_path());
            for (File dir : directories)
            {
                if (dir.getName().equals(most_recent.getName()))
                {
                    Data.getINSTANCE().setJar_filepath(mr_dir.toString() + "\\" + most_recent.getName());
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
                Data.getINSTANCE().setJar_filepath(mr_dir.toString() + "\\" + most_recent.getName());
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
     * Genereates an ArrayList of releases.
     * @return Returns an ArrayList of releases.
     */
    public ArrayList<Release> getReleases()
    {
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
}
