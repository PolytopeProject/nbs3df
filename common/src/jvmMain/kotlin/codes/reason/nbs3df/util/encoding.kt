package codes.reason.nbs3df.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


actual fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}

actual fun String.decodeBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

actual fun ByteArray.compress(): ByteArray {
    ByteArrayOutputStream().use { bos ->
        GZIPOutputStream(bos).use { gos ->
            gos.write(this)
        }
        return bos.toByteArray()
    }
}

actual fun ByteArray.decompress(): ByteArray {
    ByteArrayInputStream(this).use { bis ->
        GZIPInputStream(bis).use { gis ->
            return gis.readBytes()
        }
    }
}