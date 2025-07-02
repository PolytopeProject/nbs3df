package codes.reason.nbs3df.mod.screen.builder

import codes.reason.nbs3df.mod.screen.Element

@UiDsl
open class ScopedElementBuilder : ElementBuilder {

    private val _elements = mutableListOf<Element>()
    val elements: List<Element> get() = _elements.toList()

    fun addChild(element: Element) {
        _elements += element
    }

    inline fun <E : Element, B : ElementBuilder> addNested(
        builder: B,
        content: B.() -> Unit,
        buildElement: (B) -> E
    ) {
        builder.content()
        this.addChild(buildElement(builder))
    }

}