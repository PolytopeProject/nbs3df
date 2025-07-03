package codes.reason.nbs3df.mod.screen.builder

import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn

@UiDsl
interface ElementBuilder {
    companion object {
        inline fun root(configure: ScopedElementBuilder.() -> Unit) : Element {
            val builder = ScopedElementBuilder()
            builder.configure()
            return builder.elements.collapseIntoColumn()
        }
    }
}


