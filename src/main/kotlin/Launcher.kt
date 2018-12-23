import javafx.application.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *
 */
class Launcher
{

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>)
        {
            // These must be up to date.
            Constants.LAUNCHER_LOCATION = Launcher::class.java.protectionDomain.codeSource.location.toURI().path
            Constants.LAUNCHER_VERSION = "2.0"

            Constants.init()

            GlobalScope.launch { scanCommonLocations() }

            if (args.isNotEmpty())
            {
                for (str in args)
                {
                    Constants.LOG_LEVEL = str.toInt()
                }
            }

            //FirstPdf.main(args)
            Application.launch(LaunchUI::class.java, *args)
        }
    }
}