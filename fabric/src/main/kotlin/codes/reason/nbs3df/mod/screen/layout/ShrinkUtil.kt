package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.*

object ShrinkUtil {
    private inline fun shrinkChildren(
        elements: List<ElementWithToken>,
        amountToRemove: Int,
        getDimension: (ElementToken) -> ElementDimension,
        updateAvailableSize: (Int) -> AvailableSize
    ) {
        val elementSizes = elements.associateWith { getDimension(it.token).preferredValue }.toMutableMap()

        var remainingAmount = amountToRemove
        while (remainingAmount > 0) {
            val shrinkableChildren = elements.filter {
                getDimension(it.token).minimumValue < elementSizes[it]!!
            }

            if (shrinkableChildren.isEmpty()) break

            var largest = Int.MIN_VALUE
            var secondLargest = Int.MIN_VALUE

            for (child in shrinkableChildren) {
                val size = elementSizes[child]!!
                when {
                    size > largest -> {
                        secondLargest = largest
                        largest = size
                    }
                    size in (secondLargest + 1)..<largest -> {
                        secondLargest = size
                    }
                }
            }

            val childrenToGrow = shrinkableChildren.filter { elementSizes[it]!! == largest }

            val amountToGrow = if (secondLargest == Int.MIN_VALUE) {
                remainingAmount
            } else {
                ((largest - secondLargest) * childrenToGrow.size).coerceAtMost(remainingAmount)
            }

            val remainder = amountToGrow % childrenToGrow.size
            val eachShrink = amountToGrow / childrenToGrow.size

            childrenToGrow.forEachIndexed { index, it ->
                val baseShrinkAmount = if (index == 0) {
                    eachShrink + remainder
                } else {
                    eachShrink
                }
                val shrinkAmount = baseShrinkAmount.coerceAtMost(remainingAmount)
                if (shrinkAmount <= 0) return@forEachIndexed

                elementSizes[it] = elementSizes[it]!! - shrinkAmount
                remainingAmount -= shrinkAmount
            }
        }

        elements.forEach {
            val token = getDimension(it.token)
            val size = elementSizes[it] ?: token.preferredValue

            if (size != token.preferredValue) {
                it.token = it.element.createToken(updateAvailableSize(size))
            }
        }
    }

    fun shrinkChildrenWidth(
        elements: List<ElementWithToken>,
        availableSize: AvailableSize,
        shrinkBy: Int
    ) = shrinkChildren(
        elements = elements,
        amountToRemove = shrinkBy,
        getDimension = { it.width },
        updateAvailableSize = { availableSize.copy(width = it) }
    )

    fun shrinkChildrenHeight(
        elements: List<ElementWithToken>,
        availableSize: AvailableSize,
        shrinkBy: Int
    ) = shrinkChildren(
        elements = elements,
        amountToRemove = shrinkBy,
        getDimension = { it.height },
        updateAvailableSize = { availableSize.copy(height = it) }
    )
}