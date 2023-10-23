package dev.sanmer.sac.utils.extensions

import android.content.Context

val Context.tmpDir get() = cacheDir.resolve("tmp")

fun Context.deleteTmp() = tmpDir.deleteRecursively()