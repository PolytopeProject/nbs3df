package codes.reason.nbs3df.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement

@Composable
fun CenteredContainer(
    content: ContentBuilder<HTMLDivElement>? = null
) {
    Div(
        attrs = {
            style {
                height(100.vh)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
                gap(8.px)
                backgroundColor(rgb(0xe8, 0xe8, 0xe8))
            }
        },
        content = content
    )
}
