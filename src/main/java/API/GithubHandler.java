package API;

import GUI.Data;
import org.kohsuke.github.GitHub;

import java.io.IOException;

/**
 *
 */
public class GithubHandler
{
    public static boolean canQuerry(GitHub gitHub)
    {
        try
        {
            return gitHub.getRateLimit().remaining > 2;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static GitHub connect(String token)
    {
        if (token == null)
        {
            try
            {
                Data.setStatus("Connecting to GitHub anonymously");
                return GitHub.connectAnonymously();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (token.equals(""))
        {
            try
            {
                Data.setStatus("Connecting to GitHub anonymously");
                return GitHub.connectAnonymously();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                Data.setStatus("Connecting to GitHub using your token");
                return GitHub.connectUsingOAuth(token);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
