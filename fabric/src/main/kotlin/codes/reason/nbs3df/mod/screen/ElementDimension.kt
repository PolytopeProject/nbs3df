package codes.reason.nbs3df.mod.screen

import codes.reason.nbs3df.util.BoundedValue
import codes.reason.nbs3df.util.BoundedValue.Companion.bounded

data class ElementDimension(
    val minimumValue: BoundedValue = 0.bounded,
    val preferredValue: Int,
    val maximumValue: BoundedValue = preferredValue.bounded
)

