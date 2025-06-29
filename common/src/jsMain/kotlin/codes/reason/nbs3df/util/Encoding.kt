package codes.reason.nbs3df.util

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

actual fun ByteArray.toBase64(): String {
    val sb = StringBuilder()
    this.forEach {
        sb.append((it.toInt() and 0xFF).toChar())
    }
    return js("btoa(sb.toString())") as String
}

actual fun String.decodeBase64(): ByteArray {
    val binary = js("atob(this)") as String
    return ByteArray(binary.length) { i -> binary[i].code.toByte() }
}

@JsModule("pako")
@JsNonModule
private external object Pako {
    fun gzip(input: dynamic, options: dynamic = definedExternally): Uint8Array
    fun ungzip(input: dynamic, options: dynamic = definedExternally): Uint8Array
}

actual fun ByteArray.compress(): ByteArray {
    val input = Uint8Array(this.size)
    for (i in this.indices) {
        input[i] = this[i]
    }
    val compressed = Pako.gzip(input)
    return ByteArray(compressed.length) { compressed[it] }
}

actual fun ByteArray.decompress(): ByteArray {
    val input = Uint8Array(this.size)
    for (i in this.indices) {
        input[i] = this[i]
    }
    val decompressed = Pako.ungzip(input)
    return ByteArray(decompressed.length) { decompressed[it] }
}
