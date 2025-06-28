package codes.reason.nbs3df

import codes.reason.nbs3df.ui.App
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.jetbrains.compose.web.renderComposableInBody

@JsModule("bootstrap-icons/font/bootstrap-icons.css")
@JsNonModule
external val bootstrapIcons: dynamic

@JsModule("bootstrap/dist/css/bootstrap.min.css")
@JsNonModule
external val bootstrap: dynamic

@JsModule("./minecraft_to_df.json")
@JsNonModule
private external val rawSoundMappings: dynamic

@OptIn(ExperimentalSerializationApi::class)
val soundMappings = Json.decodeFromDynamic<Map<String, String>>(rawSoundMappings)

fun main() {
    bootstrapIcons
    bootstrap
    soundMappings

    renderComposableInBody { App() }
}