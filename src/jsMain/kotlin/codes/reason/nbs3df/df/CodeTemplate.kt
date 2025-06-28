package codes.reason.nbs3df.df

import codes.reason.nbs3df.util.compress
import codes.reason.nbs3df.util.toBase64
import codes.reason.nbs3df.util.toUint8Array
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class CodeTemplateData(val blocks: List<BlockOrBracket>) {
    fun encode(): String {
        val json = Json {
            classDiscriminator = "id"
            prettyPrint = false
        }
        println(json.encodeToJsonElement(this).toString())
        return json.encodeToJsonElement(this).toString()
            .encodeToByteArray().toUint8Array().compress().toBase64()
    }
}