package codes.reason.nbs3df.ui

import org.jetbrains.compose.web.renderComposableInBody

@JsModule("bootstrap-icons/font/bootstrap-icons.css")
@JsNonModule
external val bootstrapIcons: dynamic

@JsModule("bootstrap/dist/css/bootstrap.min.css")
@JsNonModule
external val bootstrap: dynamic

fun main() {
    bootstrapIcons
    bootstrap

    renderComposableInBody { App() }
}