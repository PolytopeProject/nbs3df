package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.*

object GrowUtil {
    private inline fun growChildren(
        elements: List<ElementWithToken>,
        additionalSize: Int,
        getDimension: (ElementToken) -> ElementDimension,
        updateAvailableSize: (Int) -> AvailableSize
    ) {
        val elementSizes = elements.associateWith { getDimension(it.token).preferredValue }.toMutableMap()

        var remainingAmount = additionalSize
        while (remainingAmount > 0) {
            val growableChildren = elements.filter {
                getDimension(it.token).maximumValue > elementSizes[it]!!
            }

            if (growableChildren.isEmpty()) break

            var smallest = Int.MAX_VALUE
            var secondSmallest = Int.MAX_VALUE

            for (child in growableChildren) {
                val size = elementSizes[child]!!
                when {
                    size < smallest -> {
                        secondSmallest = smallest
                        smallest = size
                    }

                    size in (smallest + 1) until secondSmallest -> {
                        secondSmallest = size
                    }
                }
            }

            val childrenToGrow = growableChildren.filter { elementSizes[it]!! == smallest }

            val amountToGrow = if (secondSmallest == Int.MAX_VALUE) {
                remainingAmount
            } else {
                ((secondSmallest - smallest) * childrenToGrow.size).coerceAtMost(remainingAmount)
            }

            val remainder = amountToGrow % childrenToGrow.size
            val eachGrow = amountToGrow / childrenToGrow.size

            childrenToGrow.forEachIndexed { index, it ->
                val baseGrowth = if (index == 0) {
                    eachGrow + remainder
                } else {
                    eachGrow
                }
                val growth = baseGrowth.coerceAtMost(remainingAmount)
                if (growth <= 0) return@forEachIndexed

                elementSizes[it] = elementSizes[it]!! + growth
                remainingAmount -= growth
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

    fun growChildrenWidth(
        elements: List<ElementWithToken>,
        availableSize: AvailableSize,
        growBy: Int
    ) = growChildren(
        elements = elements,
        additionalSize = growBy,
        getDimension = { it.width },
        updateAvailableSize = { availableSize.copy(width = it) }
    )

    fun growChildrenHeight(
        elements: List<ElementWithToken>,
        availableSize: AvailableSize,
        growBy: Int
    ) = growChildren(
        elements = elements,
        additionalSize = growBy,
        getDimension = { it.height },
        updateAvailableSize = { availableSize.copy(height = it) }
    )
}