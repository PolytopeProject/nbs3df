package codes.reason.nbs3df.util

expect fun ByteArray.toBase64(): String

expect fun String.decodeBase64(): ByteArray

expect fun ByteArray.compress(): ByteArray

expect fun ByteArray.decompress(): ByteArray