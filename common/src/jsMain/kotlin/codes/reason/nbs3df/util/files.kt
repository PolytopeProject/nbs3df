package codes.reason.nbs3df.util

import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun File.asByteReader(): ByteReader =
    suspendCancellableCoroutine { cont ->
        val reader = FileReader()

        reader.onload = {
            val result = reader.result
            if (result is ArrayBuffer) {
                val uint8Array = Uint8Array(result as ArrayBuffer)
                val byteArray = ByteArray(uint8Array.length) { i -> uint8Array[i] }
                cont.resume(ByteReader(byteArray))
            } else {
                cont.resumeWithException(IllegalStateException("Unexpected result type: ${result?.constructor?.name}"))
            }
        }

        reader.onerror = {
            cont.resumeWithException((reader.error ?: Throwable("Unknown FileReader error")) as Throwable)
        }

        reader.readAsArrayBuffer(this)

        cont.invokeOnCancellation {
            reader.abort()
        }
    }
