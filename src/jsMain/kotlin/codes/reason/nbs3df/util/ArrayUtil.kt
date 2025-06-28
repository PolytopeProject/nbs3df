package codes.reason.nbs3df.util

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

@JsModule("pako")
@JsNonModule
private external object Pako {
    fun gzip(input: dynamic, options: dynamic = definedExternally): Uint8Array
}

fun Uint8Array.compress(): Uint8Array {
    return Pako.gzip(this)
}

fun Uint8Array.toBase64(): String {
    val sb = StringBuilder()
    for (i in 0 until this.length) {
        sb.append((this[i].toInt() and 0xFF).toChar())
    }
    return js("btoa(sb.toString())") as String
}

fun Uint8Array.chunk(chunkSize: Int): List<Uint8Array> {
    val chunks = mutableListOf<Uint8Array>()
    var start = 0
    while (start < this.length) {
        val end = (start + chunkSize).coerceAtMost(this.length)
        chunks.add(this.subarray(start, end))
        start = end
    }
    return chunks.toList()
}

fun ByteArray.toUint8Array(): Uint8Array = Uint8Array(this.toTypedArray())
