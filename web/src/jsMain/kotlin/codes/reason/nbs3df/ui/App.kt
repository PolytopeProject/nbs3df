package codes.reason.nbs3df.ui

import androidx.compose.runtime.*
import codes.reason.nbs3df.song.NBSSong
import codes.reason.nbs3df.song.converter.SongConverter
import codes.reason.nbs3df.util.asByteReader
import org.jetbrains.compose.web.dom.*

@Composable
fun App() {
    var currentSong: NBSSong? by remember { mutableStateOf(null) }

    CenteredContainer {
        Card(image = "background.png") {
            H4(attrs = { classes("fw-bold") }) { Text("NBS to DiamondFire Converter") }
            P {
                Text(
                    "This is a tool that converts NBS files to DiamondFire code templates. " +
                            "All processing is done in the browser, no external server is used " +
                            "in the conversion process."
                )
            }

            if (currentSong != null) {
                val meta = currentSong!!.header.meta
                val metrics = currentSong!!.metrics
                val looping = currentSong!!.header.looping
                Span(attrs = { classes("d-flex", "gap-3", "align-items-center") }) {
                    BootstrapIcon("music-note-list")
                    B { Text("Convert ${meta.name} by ${meta.author}") }
                }
                Div(attrs = { classes("p-2") }) {
                    Span { Text("This song has ${metrics.noteCount} notes across ${metrics.layerCount} layers.") }
                    if (looping.enabled) {
                        FormCheck("enable-looping") {
                            BootstrapIcon("repeat")
                            B { Text("Preserve Looping") }
                        }
                    }
                    FormCheck("send-templates") {
                        BootstrapIcon("headphones")
                        B { Text("Get Player Template") }
                    }
                }
                Div(attrs = { classes("p-2", "d-flex", "gap-2", "align-items-center") }) {
                    ButtonPrimary(
                        attrs = {
                            onClick {
                                for (codeTemplateData in SongConverter.convertSong(currentSong!!)) {
                                    println(codeTemplateData.encode())
                                }
                            }
                        }
                    ) {
                        Text("Send to CodeClient")
                    }
                    ButtonSecondary {
                        Text("/give Command")
                    }
                }


            } else {
                FileUploadButton(
                    accepts = listOf(".nbs"),
                    processFile = {
                        currentSong = NBSSong.parse(it.asByteReader())
                    }
                )
            }
        }
        Span(attrs = {classes("text-black-50")}) {
            Text("Made with ")
            BootstrapIcon("heart")
            Text(" by Emily")
        }
    }
}