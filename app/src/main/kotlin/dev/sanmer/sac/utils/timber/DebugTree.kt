package dev.sanmer.sac.utils.timber

import timber.log.Timber

class DebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val newTag = tag?.let {
            val threadName = Thread.currentThread().name
            return@let "<SAC_DEBUG><$threadName>$tag"
        }

        super.log(priority, newTag, message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + "(L${element.lineNumber})"
    }
}