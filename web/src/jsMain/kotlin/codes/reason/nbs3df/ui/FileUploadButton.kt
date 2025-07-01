package codes.reason.nbs3df.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File

@Composable
fun FileUploadButton(
    accepts: List<String> = listOf(),
    processFile: suspend (File) -> Unit,
    content: ContentBuilder<HTMLButtonElement>? = null
) {
    val coroutineScope = rememberCoroutineScope()

    var inputRef by remember { mutableStateOf<HTMLInputElement?>(null) }
    var loading by remember { mutableStateOf(false) }

    Div {
        Input(InputType.File) {
            accepts.forEach { accept(it) }
            onInput {
                val file = it.target.files?.item(0)
                if (file != null && !loading) {
                    loading = true
                    coroutineScope.launch {
                        try {
                            processFile(file)
                        } finally {
                            loading = false
                        }
                    }
                }
            }
            ref {
                inputRef = it
                onDispose {
                    inputRef = null
                }
            }
            style {
                display(DisplayStyle.None)
            }
        }

        ButtonPrimary(
            attrs = {
                onClick {
                    inputRef?.click()
                }
                classes("d-flex", "gap-2", "align-items-center")
                if (loading) {
                    attr("disabled", "true")
                }
            }
        ) {
            if (content != null) {
                content()
            } else {
                BootstrapIcon("upload")
                Text("Choose File")
            }
        }
    }
}