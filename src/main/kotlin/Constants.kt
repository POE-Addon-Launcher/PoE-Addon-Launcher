import Constants.Companion.BASE_DIR
import Constants.Companion.objectMapper
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.kohsuke.github.GHRelease
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 */
class Constants
{
    companion object
    {
        var LOG_LEVEL = 5
        var BASE_DIR = "${System.getenv("LOCALAPPDATA")}${File.separator}PAL"
        var INSTALL_DIR = ""
        var LAUNCHER_LOCATION = ""
        var LAUNCHER_VERSION = ""
        var SCAN_LOG = CopyOnWriteArrayList<String>()
        var POE_PATHS_FOUND = CopyOnWriteArrayList<String>()
        var FRESH_INSTALL = false
        val ADDITIONAL_LOCATIONS = ArrayList<File>()
        val REPOSITORY = "POE-Addon-Launcher/Core"
        val GH_RELEASES = ArrayList<GHRelease>()
        var JAR_TO_RUN: File? = null

        lateinit var POE_PATHS: Array<String>
        lateinit var PAL_SETTINGS: PALSettings

        var palSettingsExists = false


        lateinit var objectMapper: ObjectMapper

        fun addToScanLog(textToAdd: String)
        {
            SCAN_LOG.add(textToAdd)
        }

        fun findReleaseByName(arg: String): GHRelease?
        {
            for (r in GH_RELEASES)
            {
                if (r.name == arg)
                {
                    return r
                }
            }
            return null
        }

        fun addToPoePaths(arg: String)
        {
            println("Found: $arg")

            if (!POE_PATHS_FOUND.contains(arg))
            {
                POE_PATHS_FOUND.add(arg)
            }
        }

        fun removeFromPoePaths(arg: String)
        {
            if (POE_PATHS_FOUND.contains(arg))
            {
                POE_PATHS_FOUND.remove(arg)
            }
        }

        fun initJackson()
        {
            println("Jackson")
            objectMapper = ObjectMapper()
                    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                    .registerModule(KotlinModule())
        }

        fun initLdPAL()
        {
            println("LauncherData.pal")
            var ldPAL = LauncherDataPAL.read()
            INSTALL_DIR = ldPAL.install_dir
        }

        fun initPoePaths()
        {
            println("Parsing PoE Paths")
            val file = File("${Constants.BASE_DIR}${File.separator}poe_paths.pal")
            POE_PATHS = objectMapper.readValue(file, Array<String>::class.java)
            for (str in POE_PATHS)
            {
                addToPoePaths(str)
            }
        }

        fun savePoePaths()
        {
            println("Saving PoE Paths")
            val file = File("${Constants.BASE_DIR}${File.separator}poe_paths.pal")

            if (file.exists())
                delete(file)

            objectMapper.writeValue(file, POE_PATHS_FOUND)
        }

        fun initCoreSettings()
        {
            println("Parsing Core Settings")
            val file = File("${Constants.BASE_DIR}${File.separator}core_settings.pal")

            if (file.exists())
            {
                var ps = objectMapper.readValue(file, PALSettings::class.java)
                PAL_SETTINGS = ps
                palSettingsExists = true
            }
            else
            {
                palSettingsExists = false
            }

        }

        fun saveCoreSettings()
        {
            println("Saving Core Settings")
            val file = File("${Constants.BASE_DIR}${File.separator}core_settings.pal")

            if (file.exists())
                delete(file)

            objectMapper.writeValue(file, PAL_SETTINGS)
        }

        fun init()
        {
            println("Initializing...")
            initJackson()

            var file = File("${Constants.BASE_DIR}${File.separator}launcher_data.pal")

            // Previous Install Exists
            if (file.exists())
            {
                initLdPAL()
                initPoePaths()
                initCoreSettings()
            }
            else
            {
                FRESH_INSTALL = true
                setDefaultLauncherSetting()
                println("Fresh Install Detected!")
            }


        }

        private fun setDefaultLauncherSetting()
        {
            INSTALL_DIR = Constants.BASE_DIR
        }

        fun createBaseFolders()
        {
            val pal = File(BASE_DIR)
            if (!pal.exists())
                pal.mkdir()

            val addons = File("${INSTALL_DIR}${File.separator}Addons")

            if (!addons.exists())
                addons.mkdir()

            val addonsTemp = File("${INSTALL_DIR}${File.separator}Addons${File.separator}temp")

            if (!addonsTemp.exists())
                addonsTemp.mkdir()

            val core = File("${INSTALL_DIR}${File.separator}Core")

            if (!core.exists())
                core.mkdir()

        }
    }
}

data class LauncherDataPAL(val install_dir: String, val launcher_location: String, val version: String)
{
    companion object
    {
        fun read(): LauncherDataPAL
        {
            val file = File("${Constants.BASE_DIR}${File.separator}launcher_data.pal")
            return Constants.objectMapper.readValue(file, LauncherDataPAL::class.java)
        }

        fun save(arg: LauncherDataPAL)
        {
            val f = File("$BASE_DIR${File.separator}launcher_data.pal")

            if (f.exists())
                delete(f)

            objectMapper.writeValue(f, arg)
        }
    }
}


data class PALSettings
(
        var wait_for_updates : Boolean,
        var loot_filter_dir : String,
        var github_api_enabled : Boolean,
        var pref_version : String,
        var filterblast_api : Boolean,
        var down_on_launch : Boolean,
        var github_token : String,
        var github_api_token_enabled : Boolean,
        var run_poe_on_launch : Boolean,
        var ahk_Folder : String
)