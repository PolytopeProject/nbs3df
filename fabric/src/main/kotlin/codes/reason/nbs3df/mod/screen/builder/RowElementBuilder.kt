package codes.reason.nbs3df.mod.screen.builder

import codes.reason.nbs3df.mod.screen.layout.GrowDirection
import codes.reason.nbs3df.mod.screen.layout.GrowElement

class RowElementBuilder : ScopedElementBuilder() {

    fun spacer() {
        this.addChild(GrowElement(GrowDirection.HORIZONTAL))
    }

}