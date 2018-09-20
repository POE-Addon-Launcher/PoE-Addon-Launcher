package Helper;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Objects;

/**
 * Object that holds relevant info from the github api.
 */
public class Release
{
    private String name = "";
    private String version = "";
    private String download_url = "";
    private int num = 0;

    public Release()
    {}

    /**
     * Release object.
     * @param jsonNode Input a jsonNode (gotten from Github API)
     */
    public Release(JsonNode jsonNode)
    {
        if (jsonNode == null)
            return;

        JsonNode name = jsonNode.get("name");
        this.version = name.toString().replace("\"", "");
        JsonNode assets = jsonNode.get("assets");
        Iterator<JsonNode> elements = assets.elements();

        if (elements.hasNext())
        {
            JsonNode zero = elements.next();
            this.name = zero.get("name").toString().replace("\"", "");
            download_url = zero.get("browser_download_url").toString().replace("\"", "");
        }

        String temp = this.name;
        num = Integer.parseInt(temp.replace("b", "").replace(".jar", ""));
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDownload_url()
    {
        return download_url;
    }

    public void setDownload_url(String download_url)
    {
        this.download_url = download_url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append(" | ");
        stringBuilder.append(version);
        stringBuilder.append(" | ");
        stringBuilder.append(download_url);
        return stringBuilder.toString();
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int num)
    {
        this.num = num;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release release = (Release) o;
        return num == release.num &&
                Objects.equals(name, release.name) &&
                Objects.equals(version, release.version) &&
                Objects.equals(download_url, release.download_url);
    }
}
