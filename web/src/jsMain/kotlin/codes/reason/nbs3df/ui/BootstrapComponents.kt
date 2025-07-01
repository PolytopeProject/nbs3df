package codes.reason.nbs3df.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.forId
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLLabelElement

@Composable
fun ButtonPrimary(
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    content: ContentBuilder<HTMLButtonElement>? = null
) {
    Button(
        attrs = {
            classes("btn", "btn-primary")
            if (attrs != null) {
                this.attrs()
            }
        },
        content = content
    )
}

@Composable
fun ButtonSecondary(
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    content: ContentBuilder<HTMLButtonElement>? = null
) {
    Button(
        attrs = {
            classes("btn", "btn-secondary")
            if (attrs != null) {
                this.attrs()
            }
        },
        content = content
    )
}

@Composable
fun Card(
    image: String? = null,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null
) {
    Div(
        attrs = {
            classes("card", "shadow")
            style {
                width(400.px)
            }
        },
        content = {
            image?.let {
                Img(
                    attrs = {
                        classes("card-img-top")
                    },
                    src = it
                )
            }
            Div(
                attrs = {
                    classes("card-body")
                },
                content = content
            )
        }
    )
}

@Composable
fun FormCheck(
    id: String,
    content: ContentBuilder<HTMLLabelElement>? = null
) {
    Div(attrs = { classes("form-check")}) {
        Input(InputType.Checkbox) {
            classes("form-check-input")
            value("")
            id(id)
        }
        Label(attrs = {
            classes("form-check-label", "d-flex", "gap-2", "align-items-center")
            forId(id)
        }, content = content)
    }
}