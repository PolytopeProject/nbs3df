package codes.reason.nbs3df.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.I

@Composable
fun BootstrapIcon(
    name: String
) {
    I(
        attrs = {
            classes("bi", "bi-$name")
        }
    )
}