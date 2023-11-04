package dev.sanmer.sac.utils.extensions

import android.content.Context
import android.content.Intent

val Context.tmpDir get() = cacheDir.resolve("tmp")

fun Context.deleteTmp() = tmpDir.deleteRecursively()

fun Context.openUrl(url: String) {
    Intent.parseUri(url, Intent.URI_INTENT_SCHEME).apply {
        startActivity(this)
    }
}