package dev.sanmer.sac.io

import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

internal interface Library {
    val name: String
    fun load()

    companion object {
        private const val PATH = "LIBSAC_JNI_PATH"
        private const val TMP = "sac-jni"

        internal fun <T: Library> T.getLibrary(): String {
            val path = System.getenv(PATH)?.let {
                val path = File(it)
                if (path.exists()) path.absolutePath else null
            }

            if (path != null) {
                return path
            }

            val library = javaClass.getResource("/${name}")
            if (library?.protocol != "jar") {
                throw LinkageError("Load $name")
            }

            val libsDir = Files.createTempDirectory(TMP).toFile()
            val libraryFile = File(libsDir, name)

            val inputStream = library.openStream()
            val outputStream = FileOutputStream(libraryFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return libraryFile.absolutePath
        }
    }
}