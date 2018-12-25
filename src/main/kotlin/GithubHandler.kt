import org.kohsuke.github.GHRelease
import org.kohsuke.github.GitHub
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 */
fun canQuerry(gitHub: GitHub): Boolean
{
    println("${gitHub.rateLimit} requests left.")
    return gitHub.rateLimit.remaining > 3
}

fun connect(): GitHub
{
    if (Constants.palSettingsExists)
    {
        if (Constants.PAL_SETTINGS.github_api_token_enabled)
        {
            if (Constants.PAL_SETTINGS.github_token != "")
            {
                return GitHub.connectUsingOAuth(Constants.PAL_SETTINGS.github_token)
            }
        }
    }
    return GitHub.connectAnonymously()
}

fun connect(arg: String): GitHub
{
    return GitHub.connectUsingOAuth(arg)
}

fun checkReleases(github: GitHub): MutableList<GHRelease>?
{
    val repo = github.getRepository(Constants.REPOSITORY)
    val releases = repo.releases
    return releases
}

fun ghDownloadbyURL(url: String, name: String)
{
    val checkFile = File("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}$name")

    if (checkFile.exists())
    {
        delete(checkFile)
    }

    val inputStream = URI.create(url).toURL().openStream()
    Files.copy(inputStream, Paths.get("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}$name"))
    Constants.JAR_TO_RUN = File("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}$name")
}