package dev.sanmer.sac.io


internal object ImplLibrary: Library {
    override val name: String = "sac-jni"

    override fun load() {
        System.loadLibrary(name)
    }
}
