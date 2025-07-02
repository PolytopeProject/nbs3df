package codes.reason.nbs3df.mod.screen.builder

import codes.reason.nbs3df.mod.screen.layout.GrowDirection
import codes.reason.nbs3df.mod.screen.layout.GrowElement

class ColumnElementBuilder : ScopedElementBuilder() {

    fun spacer() {
        this.addChild(GrowElement(GrowDirection.VERTICAL))
    }

}