package dev.sanmer.sac.io


internal object ImplLibrary: Library {
    override val name: String = "sac_jni"

    override fun load() {
        System.loadLibrary(name)
    }
}
