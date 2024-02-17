package dev.sanmer.sac.io

import java.io.Closeable
import java.io.File

class Sac(
    private val ptr: Long
) : Closeable {
    var header: SacHeader
        get() = getHeader(ptr)
        set(value) = setHeader(ptr, value)

    var first: FloatArray
        get() = getFirst(ptr)
        set(value) = setFirst(ptr, value)

    var second: FloatArray
        get() = getSecond(ptr)
        set(value) = setSecond(ptr, value)

    fun write(file: File, endian: Endian) = write(
        ptr = ptr,
        path = file.absolutePath,
        endian = endian.ordinal
    )

    override fun close() = drop(ptr)

    companion object {
        init {
            ImplLibrary.load()
        }

        @JvmStatic
        private external fun read(path: String, endian: Int): Long

        @JvmStatic
        private external fun empty(path: String, endian: Int): Long

        @JvmStatic
        private external fun write(ptr: Long, path: String, endian: Int)

        @JvmStatic
        private external fun getHeader(ptr: Long): SacHeader

        @JvmStatic
        private external fun setHeader(ptr: Long, h: SacHeader)

        @JvmStatic
        private external fun getFirst(ptr: Long): FloatArray

        @JvmStatic
        private external fun setFirst(ptr: Long, x: FloatArray)

        @JvmStatic
        private external fun getSecond(ptr: Long): FloatArray

        @JvmStatic
        private external fun setSecond(ptr: Long, y: FloatArray)

        @JvmStatic
        private external fun drop(ptr: Long)

        fun read(file: File, endian: Endian): Sac {
            val ptr = read(
                path = file.absolutePath,
                endian = endian.ordinal
            )

            return Sac(ptr)
        }

        fun empty(file: File, endian: Endian): Sac {
            val ptr = empty(
                path = file.absolutePath,
                endian = endian.ordinal
            )

            return Sac(ptr)
        }
    }
}