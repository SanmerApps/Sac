package dev.sanmer.sac.utils.timber

import timber.log.Timber

class ReleaseTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "<SAC_REL>$tag", message, t)
    }
}