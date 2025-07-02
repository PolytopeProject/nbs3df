package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementDimension
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.util.BoundedValue
import codes.reason.nbs3df.util.BoundedValue.Companion.bounded

class GrowElement(
    private val direction: GrowDirection
) : Element() {
    override fun createToken(availableSize: AvailableSize) = when (direction) {
        GrowDirection.HORIZONTAL -> ElementToken(
            width = ElementDimension(
                minimumValue = 0.bounded,
                preferredValue = availableSize.width ?: 0,
                maximumValue = BoundedValue.PositiveUnbounded,
            ),
            height = ElementDimension(
                minimumValue = 0.bounded,
                preferredValue = 0,
                maximumValue = 0.bounded
            )
        ) { _, _ -> }
        GrowDirection.VERTICAL -> ElementToken(
            width = ElementDimension(
                minimumValue = 0.bounded,
                preferredValue = 0,
                maximumValue = 0.bounded
            ),
            height = ElementDimension(
                minimumValue = 0.bounded,
                preferredValue = availableSize.height ?: 0,
                maximumValue = BoundedValue.PositiveUnbounded,
            )
        ) { _, _ -> }
    }
}
