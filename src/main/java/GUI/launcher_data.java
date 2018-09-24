package GUI;

/**
 *
 */
public class launcher_data
{
    private String install_dir;
    private String launcher_location;
    private String version = "1.4";

    private launcher_data()
    {}

    public launcher_data(String install, String launcher)
    {
        install_dir = install;
        launcher_location = launcher;
    }

    public String getInstall_dir()
    {
        return install_dir;
    }

    public void setInstall_dir(String install_dir)
    {
        this.install_dir = install_dir;
    }

    public String getLauncher_location()
    {
        return launcher_location;
    }

    public void setLauncher_location(String launcher_location)
    {
        this.launcher_location = launcher_location;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
