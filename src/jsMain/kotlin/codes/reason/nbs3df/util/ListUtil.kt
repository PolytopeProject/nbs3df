package codes.reason.nbs3df.util

fun <T> List<T>.chunk(chunkSize: Int): List<List<T>> {
    val chunks = mutableListOf<List<T>>()
    var start = 0
    while (start < this.size) {
        val end = (start + chunkSize).coerceAtMost(this.size)
        chunks.add(this.subList(start, end))
        start = end
    }
    return chunks.toList()
}