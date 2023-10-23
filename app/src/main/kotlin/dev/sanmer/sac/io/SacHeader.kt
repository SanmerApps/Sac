package dev.sanmer.sac.io

import java.io.File

@Suppress("SpellCheckingInspection")
class SacHeader(
    var delta: Float,
    var depmin: Float,
    var depmax: Float,
    var scale: Float,
    var odelta: Float,
    var b: Float,
    var e: Float,
    var o: Float,
    var a: Float,
    var t: FloatArray,
    var f: Float,
    var resp: FloatArray,
    var stla: Float,
    var stlo: Float,
    var stel: Float,
    var stdp: Float,
    var evla: Float,
    var evlo: Float,
    var evel: Float,
    var evdp: Float,
    var mag: Float,
    var user: FloatArray,
    var dist: Float,
    var az: Float,
    var baz: Float,
    var gcarc: Float,
    var depmen: Float,
    var cmpaz: Float,
    var cmpinc: Float,
    var xminimum: Float,
    var xmaximum: Float,
    var yminimum: Float,
    var ymaximum: Float,
    var nzyear: Int,
    var nzjday: Int,
    var nzhour: Int,
    var nzmin: Int,
    var nzsec: Int,
    var nzmsec: Int,
    var nvhdr: Int,
    var norid: Int,
    var nevid: Int,
    var npts: Int,
    var nwfid: Int,
    var nxsize: Int,
    var nysize: Int,
    var iftype: Int,
    var idep: Int,
    var iztype: Int,
    var iinst: Int,
    var istreg: Int,
    var ievreg: Int,
    var ievtyp: Int,
    var iqual: Int,
    var isynth: Int,
    var imagtyp: Int,
    var imagsrc: Int,
    var leven: Boolean,
    var lpspol: Boolean,
    var lovrok: Boolean,
    var lcalda: Boolean,
    var kstnm: String,
    var kevnm: String,
    var khole: String,
    var ko: String,
    var ka: String,
    var kt: Array<String>,
    var kf: String,
    var kuser0: String,
    var kuser1: String,
    var kuser2: String,
    var kcmpnm: String,
    var knetwk: String,
    var kdatrd: String,
    var kinst: String
) {
    companion object {
        fun read(file: File, endian: Endian): SacHeader {
            Sac.readHeader(file, endian).use {
                return it.h
            }
        }

        fun update(file: File, endian: Endian, block: SacHeader.() -> Unit) {
            Sac.readHeader(file, endian).use {
                it.h = it.h.apply(block)
                it.writeHeader()
            }
        }
    }
}
