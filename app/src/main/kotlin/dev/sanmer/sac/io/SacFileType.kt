package dev.sanmer.sac.io

enum class SacFileType(val iftype: Int) {
    Time(1),
    RealImag(2),
    AmpPhase (3),
    XY(4);

    companion object {
        fun valueBy(iftype: Int) = when (iftype) {
            Time.iftype -> Time
            RealImag.iftype -> RealImag
            AmpPhase.iftype -> AmpPhase
            XY.iftype -> XY
            else -> throw IllegalArgumentException()
        }
    }
}