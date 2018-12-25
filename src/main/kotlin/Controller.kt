import javafx.application.Application
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.stage.Stage
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.stage.StageStyle
import java.net.URL
import java.util.*
import javafx.stage.DirectoryChooser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javafx.scene.control.CheckBox
import javafx.scene.layout.AnchorPane
import org.kohsuke.github.GHAsset
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GitHub
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths


/**
 *
 */
class LaunchUI: Application()
{
    companion object
    {
        lateinit var stage: Stage
    }

    override fun start(primaryStage: Stage?)
    {
        val fxmlLoader = FXMLLoader()
        primaryStage!!.initStyle(StageStyle.UNDECORATED)
        val root = fxmlLoader.load<Parent>(javaClass.getResource("/ui.fxml").openStream())
        root.stylesheets.add(javaClass.getResource("/text.css").openStream().toString())
        primaryStage.title = "PAL Updater"
        primaryStage.isResizable = false
        val scene = Scene(root, 600.0, 338.0)
        scene.stylesheets.add("text.css")
        primaryStage.scene = scene
        primaryStage.icons.add(Image(javaClass.getResource("/witch.png").toString()))
        stage = primaryStage
        primaryStage.show()
    }

}

class LaunchController: Initializable
{
    private var xOffset = 0.0
    private var yOffset = 0.0

    fun onMouseDragged(mouseEvent: MouseEvent)
    {
        LaunchUI.stage.x = mouseEvent.screenX + xOffset
        LaunchUI.stage.y = mouseEvent.screenY + yOffset
    }

    fun onMousePressed(mouseEvent: MouseEvent)
    {
        xOffset = LaunchUI.stage.x - mouseEvent.screenX
        yOffset = LaunchUI.stage.y - mouseEvent.screenY
    }

    override fun initialize(location: URL?, resources: ResourceBundle?)
    {
        Constants.INSTALL_DIR = Constants.BASE_DIR
        Platform.runLater { sInstallField.text = Constants.BASE_DIR }

        updateLog()
        PoEPathsUpdater()
        writeCheckboxFromConstants()

        if (Constants.FRESH_INSTALL)
        {
            firstRunSetup(true, 0.15)
        }
        else
        {
            lsRead()
        }

        Platform.runLater {
            lpStatus.text = "Welcome Back to PAL!"
            lpStatus.isVisible = true
        }
    }

    fun firstRunSetup(b: Boolean, opacity: Double)
    {
        Platform.runLater {
            mainButtonsScreen.isVisible = !b
            settingsTabPane.isVisible = b
            launchPALimageview.opacity = opacity
            manualUpdateImageView.opacity = opacity
            checkForUpdatesImageView.opacity = opacity
        }
    }

    var cachedLogSize: Int = 0

    fun updateLog()
    {
        GlobalScope.launch {
            while (true)
            {
                delay(50)
                val _scan_log = Constants.SCAN_LOG

                if (cachedLogSize != _scan_log.size)
                {
                    Platform.runLater { scanActiveText.text = _scan_log[_scan_log.size-1] }
                    cachedLogSize = _scan_log.size
                }
            }
        }
    }

    fun PoEPathsUpdater()
    {
        GlobalScope.launch {
            var size = 0
            while (true)
            {
                delay(50)
                val _PoePaths = Constants.POE_PATHS_FOUND

                if (_PoePaths.size != size)
                {
                    for (c in size until  _PoePaths.size)
                    {
                        println(_PoePaths[c])
                        Platform.runLater { poeList.items.add(_PoePaths[c]) }
                    }
                    size = _PoePaths.size
                }
            }
        }
    }

    /**
     * Change the image of an image view.
     * @param imageView ImageView to be changed.
     * @param s String location of the image. [(resources)/{your_folder}/your_file.ext]
     */
    private fun changeImage(imageView: ImageView, s: String)
    {
        log(0, "Changing ${imageView.id} -> $s")
        Platform.runLater { imageView.image = Image(javaClass.getResource(s).toString()) }
    }

    fun pressed(mouseEvent: MouseEvent)
    {
        if (mouseEvent.source is ImageView)
        {
            val imgView: ImageView = mouseEvent.source as ImageView
            when (imgView.id)
            {
                "imgViewminimize"           -> LaunchUI.stage.isIconified = true
                "imgviewExit"               -> System.exit(42)
                "imgviewsettings"           -> showHideSettings()
                "launchPALimageview"        -> launch_jar()
                "manualUpdateImageView"     -> manualUpdate(true)
                "checkForUpdatesImageView"  -> checkForUpdates()
            }
        }
        else
        {
            System.err.println("Mouse Click property on something that shouldn't be there, report to rizlim!")
            log(1, "$mouseEvent")
        }
    }

    private fun manualUpdate(b: Boolean)
    {
        if (!Constants.FRESH_INSTALL)
        {
            Platform.runLater {
                settingsTabPane.isVisible = false
                mainButtonsScreen.isVisible = true
                lpStatus.text = "Manual Update\nPlease, enter the download URL of\nthe jar you wish to manually update to.\n\n\nor type dbg to open the info window"
                bManUpdateField.isVisible = b
                bManUse.isVisible = b
                txtDump.isVisible = !b
                lpStatus.isVisible = b
            }
        }
    }

    private fun checkForUpdates()
    {
        if (!Constants.FRESH_INSTALL)
        {
            Platform.runLater {
                settingsTabPane.isVisible = false
                mainButtonsScreen.isVisible = true
                lpStatus.text = "Checking for updates...\nPAL will launch when we're done checking/updating!"
                bManUpdateField.isVisible = false
                bManUse.isVisible = false
                txtDump.isVisible = false
                lpStatus.isVisible = true
            }

            GlobalScope.launch {
                lateinit var gh: GitHub

                gh = if (pcGithubToken.text != "")
                {
                    connect(pcGithubToken.text)
                }
                else
                {
                    connect()
                }


                if (canQuerry(gh))
                {
                    lpStatusAdd("\n\nConnect Succesful\nYou have ${gh.rateLimit.remaining} requests left which reset at:\n${gh.rateLimit.resetDate}")
                    val repo = gh.getRepository(Constants.REPOSITORY)
                    val mr = repo.latestRelease
                    val asset = find_jarAsset(mr.assets)
                    if (asset != null)
                    {
                        val file = File("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}${asset.name}")
                        if (file.exists())
                        {
                            lpStatusAdd("\nYou're up to date!")
                            Constants.JAR_TO_RUN = file
                        }
                        else
                        {
                            lpStatusAdd("\nDownload in progress!")
                            ghDownloadbyURL(asset.browserDownloadUrl, asset.name)
                            lpStatusAdd("\nDownload done!")
                        }



                        lpStatusAdd("\n" +
                                "Launching PAL hf mapping!")
                        launch_jar()
                    }
                }
                else
                {
                    lpStatusAdd("\n" +
                            "You're out of Github requests get an API token or wait until\n" +
                            "${gh.rateLimit.resetDate}")
                }
            }
        }
    }

    private fun lpStatusAdd(arg: String)
    {
        Platform.runLater {
            lpStatus.text += arg
        }
    }

    private fun launch_jar()
    {
        if (!Constants.FRESH_INSTALL)
        {
            val jar_to_run = Constants.JAR_TO_RUN

            if (jar_to_run == null)
            {
                // Check for updates
                checkForUpdates()
            }
            else
            {
                Runtime.getRuntime().exec("java -jar \"${jar_to_run.path}\"")
                System.exit(1)
            }
        }
    }

    private fun showHideSettings()
    {
        Platform.runLater {
            val mod = settingsTabPane.isVisible
            settingsTabPane.isVisible = !mod
            mainButtonsScreen.isVisible = mod
        }
    }

    fun hide(mouseEvent: MouseEvent)
    {
        if (mouseEvent.source is ImageView)
        {
            val imgView: ImageView = mouseEvent.source as ImageView
            when (imgView.id)
            {
                "imgViewminimize"           -> changeImage(imgView, "minimize.png")
                "imgviewExit"               -> changeImage(imgView, "cancel0.png")
                "imgviewsettings"           -> changeImage(imgView, "settings.png")
                "launchPALimageview"        -> changeImage(imgView, "lp0.png")
                "manualUpdateImageView"     -> changeImage(imgView, "mu0.png")
                "checkForUpdatesImageView"  -> changeImage(imgView, "cu0.png")
            }
        }
        else
        {
            System.err.println("Mouse Exit property on something that shouldn't be there, report to rizlim!")
            log(1, "$mouseEvent")
        }
    }

    fun show(mouseEvent: MouseEvent)
    {
        if (mouseEvent.source is ImageView)
        {
            val imgView: ImageView = mouseEvent.source as ImageView
            when (imgView.id)
            {
                "imgViewminimize"           -> changeImage(imgView, "minimize_hl.png")
                "imgviewExit"               -> changeImage(imgView, "cancel.png")
                "imgviewsettings"           -> changeImage(imgView, "settings_hl.png")
                "launchPALimageview"        -> changeImage(imgView, "lp1.png")
                "manualUpdateImageView"     -> changeImage(imgView, "mu1.png")
                "checkForUpdatesImageView"  -> changeImage(imgView, "cu1.png")
            }
        }
        else
        {
            System.err.println("Mouse Over property on something that shouldn't be there, report to rizlim!")
            log(1, "$mouseEvent")
        }
    }

    fun log(errcode: Int, arg: String)
    {
        Platform.runLater {
            val log_lvl = Constants.LOG_LEVEL
            if (log_lvl <= errcode)
            {
                txtDump.text = "${txtDump.text}\n$arg"
                txtDump.scrollTop = Double.MAX_VALUE
            }

        }
    }

    fun AutoScan(actionEvent: ActionEvent)
    {
        GlobalScope.launch {

            Platform.runLater {
                scanCommonLocations()
                settingsTabPane.selectionModel.select(0)
            }
        }
    }

    fun changeInstallDir(actionEvent: ActionEvent)
    {
        GlobalScope.launch {
            Platform.runLater {
                var file:File? = browse("Browse for a PAL install directory, must be empty!")

                if (file == null)
                {
                    Constants.INSTALL_DIR = Constants.BASE_DIR
                    Platform.runLater { sInstallField.text = Constants.BASE_DIR }
                }
                else
                {
                    var f = File("${file.path}${File.separator}PAL")
                    if (!f.exists())
                    {
                        f.mkdir()
                    }
                    else
                    {
                        f = mkdirUntilEmpty(f)
                    }
                    Constants.INSTALL_DIR = f.path
                    Platform.runLater { sInstallField.text = f.path }
                }
            }
        }
    }

    /**
     * Method for Opening the DirectoryChooser.
     */
    fun browse(title: String): File?
    {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = title
        return directoryChooser.showDialog(LaunchUI.stage)
    }

    fun lsReset(actionEvent: ActionEvent)
    {
        Platform.runLater {
            lsInstallDir.text = Constants.BASE_DIR
            lsLauncherLoc.text = Launcher::class.java.protectionDomain.codeSource.location.toURI().path
            lsVersion.text = "2.0"
        }
    }

    fun lsSave(actionEvent: ActionEvent)
    {
        LauncherDataPAL.save(LauncherDataPAL(lsInstallDir.text, lsLauncherLoc.text, lsVersion.text))
    }

    fun lsRead()
    {
        Platform.runLater {
            lsInstallDir.text = Constants.INSTALL_DIR
            lsLauncherLoc.text = Constants.LAUNCHER_LOCATION
            lsVersion.text = Constants.LAUNCHER_VERSION
        }
    }

    fun showLog(actionEvent: ActionEvent)
    {
        Platform.runLater { settingsTabPane.selectionModel.select(4) }
    }

    fun writeCheckboxFromConstants()
    {
        if (Constants.palSettingsExists)
        {
            GlobalScope.launch {
                Platform.runLater {
                    pcAHKFOlder.text = Constants.PAL_SETTINGS.ahk_Folder
                    pcDownOnLaunch.isSelected = Constants.PAL_SETTINGS.down_on_launch
                    pcGithubAPIEnabled.isSelected = Constants.PAL_SETTINGS.github_api_enabled
                    pcGithubApiTokenEnabled.isSelected = Constants.PAL_SETTINGS.github_api_token_enabled
                    pcGithubToken.text = Constants.PAL_SETTINGS.github_token
                    pcLootFilterDir.text = Constants.PAL_SETTINGS.loot_filter_dir
                    pcPrefVersion.text = Constants.PAL_SETTINGS.pref_version
                    pcRunPoeOnLaunch.isSelected = Constants.PAL_SETTINGS.run_poe_on_launch
                    pcUseFilterblastAPI.isSelected = Constants.PAL_SETTINGS.filterblast_api
                    pcWaitForUpdates.isSelected = Constants.PAL_SETTINGS.wait_for_updates
                }
            }
        }
    }

    fun syncPALSettings()
    {
        if (Constants.palSettingsExists)
        {
            Constants.PAL_SETTINGS.ahk_Folder = pcAHKFOlder.text
            Constants.PAL_SETTINGS.down_on_launch = pcDownOnLaunch.isSelected
            Constants.PAL_SETTINGS.github_api_enabled = pcGithubAPIEnabled.isSelected
            Constants.PAL_SETTINGS.github_api_token_enabled = pcGithubApiTokenEnabled.isSelected
            Constants.PAL_SETTINGS.github_token = pcGithubToken.text
            Constants.PAL_SETTINGS.loot_filter_dir = pcLootFilterDir.text
            Constants.PAL_SETTINGS.pref_version = pcPrefVersion.text
            Constants.PAL_SETTINGS.run_poe_on_launch = pcRunPoeOnLaunch.isSelected
            Constants.PAL_SETTINGS.filterblast_api = pcUseFilterblastAPI.isSelected
            Constants.PAL_SETTINGS.wait_for_updates =  pcWaitForUpdates.isSelected
        }
    }

    fun addscanlocation(actionEvent: ActionEvent)
    {
        val res = browse("Select a folder to add to the auto scan!")
        if (res != null)
        {
            Constants.ADDITIONAL_LOCATIONS.add(res)
            Platform.runLater { txtScanLocations.text = "${txtScanLocations.text} & ${res.path}" }
        }
    }

    fun addPoeFolder(actionEvent: ActionEvent)
    {
        val f = browse("Browse to your PoE Folder")
        if (f != null)
        {
            recursiveScan(f)
        }
    }

    fun removePoeFolder(actionEvent: ActionEvent)
    {
        if (poeList.selectionModel.selectedItem != null)
        {
            Constants.removeFromPoePaths(poeList.selectionModel.selectedItem)
            poeList.items.remove(poeList.selectionModel.selectedItem)
        }
    }

    fun saveMainSettings(actionEvent: ActionEvent)
    {
        if (Constants.FRESH_INSTALL)
        {
            if (Constants.POE_PATHS_FOUND.size == 0)
            {
                return
            }
            else
            {
                firstRunSetup(false, 1.0)
                Constants.FRESH_INSTALL = false
            }
        }
        Constants.createBaseFolders()
        Constants.INSTALL_DIR = sInstallField.text
        LauncherDataPAL.save(LauncherDataPAL(Constants.INSTALL_DIR, Constants.LAUNCHER_LOCATION, Constants.LAUNCHER_VERSION))
        Constants.savePoePaths()

    }

    fun savePALsettings(actionEvent: ActionEvent)
    {
        syncPALSettings()
        Constants.saveCoreSettings()
    }

    /**
     * Also saves to Constants
     */
    fun displayReleases(arg: MutableList<GHRelease>?)
    {
        if (arg != null)
        {
            for (r in arg)
            {
                sListViewOlderVersions.items.add(r.name)
                Constants.GH_RELEASES.add(r)
            }
        }
    }


    fun ghSearch(actionEvent: ActionEvent)
    {
        val gh = connect()
        if (canQuerry(gh))
        {
            Platform.runLater {
                sButtonErrorText.text = "You have ${gh.rateLimit.remaining} GitHub API requests left\nThese reset at: ${gh.rateLimit.resetDate}"
                sButtonErrorText.isVisible = true
            }
            val releases = checkReleases(gh)
            displayReleases(releases)
        }
        else
        {
            Platform.runLater {
                sButtonErrorText.text = "You are out of GitHub API requests!\nGet an API token or wait until: ${gh.rateLimit.resetDate}"
                sButtonErrorText.isVisible = true
            }
        }
    }

    fun downloadSelectedFromGithub()
    {
        GlobalScope.launch {
            val sel = sListViewOlderVersions.selectionModel.selectedItem
            log(1, sel)
            log(3, "Called downloadSelectedFromGithub() with selected item: $sel")

            if (sel != null)
            {
                val release = Constants.findReleaseByName(sel)
                log(1, release.toString())
                if (release != null)
                {
                    // Start download
                    val asset = find_jarAsset(release.assets)
                    log(1, asset.toString())

                    if (asset != null)
                    {
                        val checkFile = File("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}${asset.name}")

                        if (checkFile.exists())
                        {
                            log(5, "Deleting ${asset.name} from ${Constants.INSTALL_DIR}${File.separator}Core because it already exists.")
                            delete(checkFile)
                        }

                        log(5, "Found: ${asset.name} with download url: ${asset.browserDownloadUrl}")
                        val inputStream = URI.create(asset.browserDownloadUrl).toURL().openStream()
                        log(5, "Downloading: ${asset.name} with url: ${asset.browserDownloadUrl}")
                        Files.copy(inputStream, Paths.get("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}${asset.name}"))
                        log(5, "Download completed file saved to: ${Constants.INSTALL_DIR}${File.separator}Core${File.separator}${asset.name}")
                        Constants.JAR_TO_RUN = File("${Constants.INSTALL_DIR}${File.separator}Core${File.separator}${asset.name}")
                    }

                }
            }
        }
    }

    fun find_jarAsset(assets: MutableList<GHAsset>): GHAsset?
    {
        for (a in assets)
        {
            if (a.name.contains(".jar"))
                return a
        }
        return null
    }

    fun manUpdate(actionEvent: ActionEvent)
    {
        if (bManUpdateField.text == "dbg")
        {
            manualUpdate(false)
        }
        else
        {
            val txt = bManUpdateField.text.split("/")
            lateinit var name: String
            for (s in txt)
            {
                if (s.contains(".jar"))
                {
                    name = s
                }
            }
            ghDownloadbyURL(bManUpdateField.text, name)
            launch_jar()
        }
    }

    @FXML
    private lateinit var sInstallField: TextField

    @FXML
    private lateinit var scanActiveText: Text

    @FXML
    private lateinit var launchPALimageview: ImageView

    @FXML
    private lateinit var manualUpdateImageView: ImageView

    @FXML
    private lateinit var checkForUpdatesImageView: ImageView

    @FXML
    private lateinit var imgviewsettings: ImageView

    @FXML
    private lateinit var imgviewExit: ImageView

    @FXML
    private lateinit var imgViewminimize: ImageView

    @FXML
    private lateinit var settingsText: Text

    @FXML
    private lateinit var sLog: Button

    @FXML
    private lateinit var sScanForMe: Button


    @FXML
    private lateinit var poeList: ListView<String>

    @FXML
    private lateinit var sListViewOlderVersions: ListView<String>

    @FXML
    private lateinit var bSearchGithub: Button

    @FXML
    private lateinit var sButtonErrorText: Text

    @FXML
    private lateinit var sThisVersionWillBeUsed: Text

    @FXML
    private lateinit var sDownloadProgressText: Text

    @FXML
    private lateinit var scanLog: Tab

    @FXML
    private lateinit var sAddScanLocation: Button

    @FXML
    private lateinit var sScanAgain: Button

    @FXML
    private lateinit var txtDump: TextArea

    @FXML
    private lateinit var lsInstallDir: TextField

    @FXML
    private lateinit var lsLauncherLoc: TextField

    @FXML
    private lateinit var lsVersion: TextField

    @FXML
    private lateinit var lsSave: Button

    @FXML
    private lateinit var lsReset: Button

    @FXML
    private lateinit var txtScanLocations: Text

    @FXML
    private lateinit var settingsTabPane: TabPane

    @FXML
    private lateinit var pcWaitForUpdates: CheckBox

    @FXML
    private lateinit var pcGithubAPIEnabled: CheckBox

    @FXML
    private lateinit var pcUseFilterblastAPI: CheckBox

    @FXML
    private lateinit var pcDownOnLaunch: CheckBox

    @FXML
    private lateinit var pcGithubApiTokenEnabled: CheckBox

    @FXML
    private lateinit var pcRunPoeOnLaunch: CheckBox

    @FXML
    private lateinit var pcGithubToken: TextField

    @FXML
    private lateinit var pcAHKFOlder: TextField

    @FXML
    private lateinit var pcPrefVersion: TextField

    @FXML
    private lateinit var pcLootFilterDir: TextField

    @FXML
    private lateinit var pcSave: Button

    @FXML
    private lateinit var mainButtonsScreen: AnchorPane

    @FXML
    private lateinit var lpStatus: Text

    @FXML
    private lateinit var bManUpdateField: TextField

    @FXML
    private lateinit var bManUse: Button

}