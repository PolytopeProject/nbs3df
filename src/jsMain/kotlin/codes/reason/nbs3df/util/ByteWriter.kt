package codes.reason.nbs3df.util

import org.khronos.webgl.Uint8Array

class ByteWriter {

    private val list: MutableList<Byte> = mutableListOf()

    fun writeByte(byte: Byte) {
        list.add(byte)
    }

    fun writeByte(byte: Int) {
        list.add(byte.toByte())
    }

    fun writeShort(value: Short) {
        writeByte((value.toInt() shr 0).toByte())
        writeByte((value.toInt() shr 8).toByte())
    }

    fun writeShort(value: Int) {
        writeByte((value shr 0).toByte())
        writeByte((value shr 8).toByte())
    }


    fun toByteArray() = Uint8Array(ByteArray(list.size) { list[it] }.toTypedArray())

}