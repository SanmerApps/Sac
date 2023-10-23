package dev.sanmer.sac.io

import java.io.Closeable
import java.io.File

class Sac(
    private val ptr: Long
) : Closeable {
    var h: SacHeader
        get() = getHeader(ptr)
        set(value) = setHeader(ptr, value)

    var x: FloatArray
        get() = getX(ptr)
        set(value) = setX(ptr, value)

    var y: FloatArray
        get() = getY(ptr)
        set(value) = setY(ptr, value)

    fun writeHeader() = wh(ptr)
    fun setEndian(endian: Endian) = setEndian(ptr, endian.ordinal)
    fun write() = w(ptr)
    fun writeTo(file: File) = wt(ptr, file.absolutePath)
    override fun close() = drop(ptr)

    companion object {
        init {
            System.loadLibrary("sac-jni")
        }

        @JvmStatic
        private external fun rh(path: String, endian: Int): Long

        @JvmStatic
        private external fun r(path: String, endian: Int): Long

        @JvmStatic
        private external fun wh(ptr: Long)

        @JvmStatic
        private external fun w(ptr: Long)

        @JvmStatic
        private external fun wt(ptr: Long, path: String)

        @JvmStatic
        private external fun getHeader(ptr: Long): SacHeader

        @JvmStatic
        private external fun setHeader(ptr: Long, h: SacHeader)

        @JvmStatic
        private external fun getX(ptr: Long): FloatArray

        @JvmStatic
        private external fun setX(ptr: Long, x: FloatArray)

        @JvmStatic
        private external fun getY(ptr: Long): FloatArray

        @JvmStatic
        private external fun setY(ptr: Long, y: FloatArray)

        @JvmStatic
        private external fun setEndian(ptr: Long, endian: Int)

        @JvmStatic
        private external fun drop(ptr: Long)

        fun readHeader(file: File, endian: Endian): Sac {
            val ptr = rh(
                path = file.absolutePath,
                endian = endian.ordinal
            )

            return Sac(ptr)
        }

        fun read(file: File, endian: Endian): Sac {
            val ptr = r(
                path = file.absolutePath,
                endian = endian.ordinal
            )

            return Sac(ptr)
        }
    }
}