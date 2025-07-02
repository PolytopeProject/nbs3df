package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementDimension
import codes.reason.nbs3df.mod.screen.ElementToken

object EmptyElement : Element() {
    override fun createToken(availableSize: AvailableSize) = ElementToken(
        width = ElementDimension(
            preferredValue = 0
        ),
        height = ElementDimension(
            preferredValue = 0
        )
    ) { _, _ -> }
}