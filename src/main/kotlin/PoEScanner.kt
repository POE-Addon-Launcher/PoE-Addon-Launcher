import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern


/**
 *
 */
fun recursiveScan(location: File)
{
    val pattern: Pattern = Pattern.compile("pathofexile.*\\.exe", Pattern.CASE_INSENSITIVE)
    location.walkTopDown().forEach {
        Constants.SCAN_LOG.add(it.path)
        GlobalScope.launch {
            if (pattern.matcher(it.name).matches())
            {
                Constants.addToPoePaths(it.path)
            }
        }

    }
    /*
    for (file in location.listFiles())
    {
        if (file != null)
        {
            Constants.SCAN_LOG.add(file.path)

            //println(file.name)
            if (file.isDirectory)
            {
                if (!file.name.contains("$"))
                    recursiveScan(file)
            }
            else
            {
                val pattern: Pattern = Pattern.compile("pathofexile.*\\.exe", Pattern.CASE_INSENSITIVE)
                if (pattern.matcher(file.name).matches())
                {
                    // TODO: Store found
                    println(file.name)

                }
            }
        }

    }*/
}

fun scanCommonLocations()
{
    val PROGRAM_FILES_GGG = File("C:\\Program Files\\GGG")
    val PROGRAM_FILES_X86_GGG = File("C:\\Program Files (x86)\\GGG")
    val PROGRAM_FILES = File("C:\\Program Files\\Steam")
    val PROGRAM_FILES_X86 = File("C:\\Program Files (x86)\\Steam")
    val GAMES = File("C:\\Games")

    when (true)
    {
        PROGRAM_FILES_GGG.exists() -> GlobalScope.launch { recursiveScan(PROGRAM_FILES) }
        PROGRAM_FILES_X86_GGG.exists() -> GlobalScope.launch { recursiveScan(PROGRAM_FILES_X86) }
        PROGRAM_FILES.exists() -> GlobalScope.launch { recursiveScan(PROGRAM_FILES) }
        PROGRAM_FILES_X86.exists() -> GlobalScope.launch { recursiveScan(PROGRAM_FILES_X86) }
        GAMES.exists() -> GlobalScope.launch { recursiveScan(GAMES) }
    }

    for (file in Constants.ADDITIONAL_LOCATIONS)
        when (true)
        {
            file.exists() -> GlobalScope.launch { recursiveScan(file) }
        }

}

fun mkdirUntilEmpty(file: File): File
{
    if (file.exists())
    {
        // Empty Directory
        if (file.listFiles().isEmpty())
        {
            return file
        }
        else
        {
            val f = File("${file.path}${File.separator}PAL")
            if (!f.exists())
                f.mkdir()
            return mkdirUntilEmpty(f)
        }
    }
    return File(Constants.BASE_DIR)
}

fun rm(name: String)
{
    rm(File(name))
}

fun rm(file: File)
{
    if (file.exists())
        if (file.isDirectory)
            for (f in file.listFiles())
                rm(f)
    delete(file)
}

fun delete(file: File)
{
    System.err.println("Deleting: ${file.path}")
    file.delete()
}