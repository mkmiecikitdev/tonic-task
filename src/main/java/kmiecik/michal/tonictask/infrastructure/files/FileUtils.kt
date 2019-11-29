package kmiecik.michal.tonictask.infrastructure.files

import java.util.*

object FileUtils {

    fun loadProperty(fileName: String, property: String): String {
        return javaClass.classLoader.getResourceAsStream(fileName).let {
            val prop = Properties()
            prop.load(it)
            it.close()
            prop.getProperty(property)
        }
    }

}