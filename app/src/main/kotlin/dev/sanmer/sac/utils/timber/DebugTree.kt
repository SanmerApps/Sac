package dev.sanmer.sac.utils.timber

import timber.log.Timber

class DebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "<SAC_DEBUG>$tag", message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + "(L${element.lineNumber})"
    }
}