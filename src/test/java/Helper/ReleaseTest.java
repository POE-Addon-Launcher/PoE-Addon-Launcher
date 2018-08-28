package Helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 *
 */
public class ReleaseTest
{
    Release r1;
    final String JSON = "[{\"url\":\"https://api.github.com/repos/POE-Addon-Launcher/Core/releases/12614169\",\"assets_url\":\"https://api.github.com/repos/POE-Addon-Launcher/Core/releases/12614169/assets\",\"upload_url\":\"https://uploads.github.com/repos/POE-Addon-Launcher/Core/releases/12614169/assets{?name,label}\",\"html_url\":\"https://github.com/POE-Addon-Launcher/Core/releases/tag/b1\",\"id\":12614169,\"node_id\":\"MDc6UmVsZWFzZTEyNjE0MTY5\",\"tag_name\":\"b1\",\"target_commitish\":\"master\",\"name\":\"b1\",\"draft\":false,\"author\":{\"login\":\"POE-Addon-Launcher\",\"id\":42203962,\"node_id\":\"MDQ6VXNlcjQyMjAzOTYy\",\"avatar_url\":\"https://avatars2.githubusercontent.com/u/42203962?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/POE-Addon-Launcher\",\"html_url\":\"https://github.com/POE-Addon-Launcher\",\"followers_url\":\"https://api.github.com/users/POE-Addon-Launcher/followers\",\"following_url\":\"https://api.github.com/users/POE-Addon-Launcher/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/POE-Addon-Launcher/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/POE-Addon-Launcher/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/POE-Addon-Launcher/subscriptions\",\"organizations_url\":\"https://api.github.com/users/POE-Addon-Launcher/orgs\",\"repos_url\":\"https://api.github.com/users/POE-Addon-Launcher/repos\",\"events_url\":\"https://api.github.com/users/POE-Addon-Launcher/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/POE-Addon-Launcher/received_events\",\"type\":\"User\",\"site_admin\":false},\"prerelease\":false,\"created_at\":\"2018-08-28T15:25:00Z\",\"published_at\":\"2018-08-28T15:25:34Z\",\"assets\":[{\"url\":\"https://api.github.com/repos/POE-Addon-Launcher/Core/releases/assets/8426547\",\"id\":8426547,\"node_id\":\"MDEyOlJlbGVhc2VBc3NldDg0MjY1NDc=\",\"name\":\"b1.jar\",\"label\":null,\"uploader\":{\"login\":\"POE-Addon-Launcher\",\"id\":42203962,\"node_id\":\"MDQ6VXNlcjQyMjAzOTYy\",\"avatar_url\":\"https://avatars2.githubusercontent.com/u/42203962?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/POE-Addon-Launcher\",\"html_url\":\"https://github.com/POE-Addon-Launcher\",\"followers_url\":\"https://api.github.com/users/POE-Addon-Launcher/followers\",\"following_url\":\"https://api.github.com/users/POE-Addon-Launcher/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/POE-Addon-Launcher/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/POE-Addon-Launcher/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/POE-Addon-Launcher/subscriptions\",\"organizations_url\":\"https://api.github.com/users/POE-Addon-Launcher/orgs\",\"repos_url\":\"https://api.github.com/users/POE-Addon-Launcher/repos\",\"events_url\":\"https://api.github.com/users/POE-Addon-Launcher/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/POE-Addon-Launcher/received_events\",\"type\":\"User\",\"site_admin\":false},\"content_type\":\"application/octet-stream\",\"state\":\"uploaded\",\"size\":808,\"download_count\":2,\"created_at\":\"2018-08-28T15:25:30Z\",\"updated_at\":\"2018-08-28T15:25:31Z\",\"browser_download_url\":\"https://github.com/POE-Addon-Launcher/Core/releases/download/b1/b1.jar\"}],\"tarball_url\":\"https://api.github.com/repos/POE-Addon-Launcher/Core/tarball/b1\",\"zipball_url\":\"https://api.github.com/repos/POE-Addon-Launcher/Core/zipball/b1\",\"body\":\"b1\"}]\n";

    @Before
    public void before() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(JSON);
        Iterator<JsonNode> elements = node.elements();
        JsonNode jason_element = elements.next();
        r1 = new Release(jason_element);
    }

    @Test
    public void constructorNull()
    {
        r1 = new Release(null);
        assertEquals("", r1.getVersion());
        assertEquals("", r1.getName());
        assertEquals("", r1.getDownload_url());
        assertEquals(0, r1.getNum());
    }

    @Test
    public void getVersion()
    {
        assertEquals("b1", r1.getVersion());
    }

    @Test
    public void setVersion()
    {
        r1.setVersion("b2");
        assertEquals("b2", r1.getVersion());
    }

    @Test
    public void getDownload_url()
    {
        r1.setDownload_url("test");
        assertEquals("test", r1.getDownload_url());
    }

    @Test
    public void getName()
    {
        r1.setName("test");
        assertEquals("test", r1.getName());
    }

    @Test
    public void testtoString()
    {
        assertEquals("b1.jar | b1 | https://github.com/POE-Addon-Launcher/Core/releases/download/b1/b1.jar", r1.toString());
    }

    @Test
    public void getNum()
    {
        r1.setNum(0);
        assertEquals(0, r1.getNum());
    }

    @Test
    public void equalsTest()
    {
        r1 = new Release(null);
        Release r2 = new Release(null);
        assertTrue(r2.equals(r1));
    }
}