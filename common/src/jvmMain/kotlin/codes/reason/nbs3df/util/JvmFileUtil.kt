package codes.reason.nbs3df.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun File.toByteReader(): ByteReader =
    withContext(Dispatchers.IO) {
        ByteReader(readBytes())
    }