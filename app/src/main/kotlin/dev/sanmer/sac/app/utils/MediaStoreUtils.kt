package dev.sanmer.sac.app.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.system.Os
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import java.io.File

object MediaStoreUtils {
    fun getDisplayNameForUri(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().name
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        val cr = context.contentResolver
        cr.query(uri, projection, null, null, null)?.use { cursor ->
            val displayNameColumn = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(displayNameColumn)
            }
        }

        return uri.toString()
    }

    fun getAbsolutePathForUri(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().absolutePath
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        val newUri = try {
            checkNotNull(DocumentFile.fromTreeUri(context, uri)?.uri)
        } catch (e: Exception) {
            uri
        }

        val cr =  context.contentResolver
        return cr.openFileDescriptor(newUri, "r")?.use {
            Os.readlink("/proc/self/fd/${it.fd}")
        } ?: uri.toString()
    }

    fun getAbsoluteFileForUri(context: Context, uri: Uri) =
        File(getAbsolutePathForUri(context, uri))

    fun copyTo(context: Context, uri: Uri, dir: File): File {
        val cr =  context.contentResolver
        val tmp = dir.resolve(getDisplayNameForUri(context, uri))

        cr.openInputStream(uri)?.use { input ->
            tmp.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tmp
    }
}