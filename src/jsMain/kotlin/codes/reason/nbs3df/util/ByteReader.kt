package codes.reason.nbs3df.util

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ByteReader(private val data: Uint8Array) {
    private var currentIndex = 0

    fun readByte() : Byte {
        if (currentIndex >= data.length) {
            throw IndexOutOfBoundsException("No more bytes to read from array")
        }
        return data[currentIndex++]
    }

    fun readUByte() : Int = readByte().toInt() and 0xFF

    fun readShort() : Short {
        val b1 = readUByte()
        val b2 = readUByte()

        return ((b2 shl 8) or b1).toShort()
    }

    fun readInt(): Int {
        val b0 = readUByte()
        val b1 = readUByte()
        val b2 = readUByte()
        val b3 = readUByte()
        return (b3 shl 24) or (b2 shl 16) or (b1 shl 8) or b0
    }

    fun readString(): String {
        val length = readInt()
        val builder = StringBuilder()
        for (i in 0 until length) {
            var c = this.readByte().toInt().toChar()
            if (c == 0x0D.toChar()) {
                c = ' '
            }
            builder.append(c)
        }
        return builder.toString()
    }

    fun readBoolean(): Boolean = readByte() != 0.toByte()

}

suspend fun File.asByteReader(): ByteReader =
    suspendCoroutine { cont ->
        val reader = FileReader()

        reader.onload = {
            val result = reader.result
            if (result is ArrayBuffer) {
                cont.resume(ByteReader(Uint8Array(result as ArrayBuffer)))
            } else {
                cont.resumeWithException(IllegalStateException("Unexpected result type: ${result?.constructor?.name}"))
            }
        }

        reader.onerror = {
            cont.resumeWithException((reader.error ?: Throwable("Unknown FileReader error")) as Throwable)
        }

        reader.readAsArrayBuffer(this)
    }