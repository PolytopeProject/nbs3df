package codes.reason.nbs3df.util

sealed class BoundedValue : Comparable<BoundedValue> {

    companion object {
        fun <T> List<T>.sumOfBounded(selector: (T) -> BoundedValue) : BoundedValue {
            var currentValue: BoundedValue = Bounded(0)
            for (t in this) {
                currentValue += selector(t)
            }
            return currentValue
        }

        val Int.bounded: BoundedValue
            get() = Bounded(this)
    }
    data class Bounded(val value: Int) : BoundedValue() {}
    data object PositiveUnbounded : BoundedValue() {}
    data object NegativeUnbounded : BoundedValue() {}

    operator fun plus(other: BoundedValue): BoundedValue = when {
        this is PositiveUnbounded || other is PositiveUnbounded -> PositiveUnbounded
        this is NegativeUnbounded || other is NegativeUnbounded -> NegativeUnbounded
        this is Bounded && other is Bounded -> {
            val sum = this.value.toLong() + other.value.toLong()
            when {
                sum > Int.MAX_VALUE -> PositiveUnbounded
                sum < Int.MIN_VALUE -> NegativeUnbounded
                else -> Bounded(sum.toInt())
            }
        }
        else -> throw IllegalStateException("Unhandled BoundedValue type")
    }

    operator fun plus(other: Int): BoundedValue = this + Bounded(other)

    override operator fun compareTo(other: BoundedValue): Int = when {
        this is PositiveUnbounded && other is PositiveUnbounded -> 0
        this is PositiveUnbounded -> 1
        other is PositiveUnbounded -> -1

        this is NegativeUnbounded && other is NegativeUnbounded -> 0
        this is NegativeUnbounded -> -1
        other is NegativeUnbounded -> 1

        this is Bounded && other is Bounded -> this.value.compareTo(other.value)

        else -> throw IllegalStateException("Unhandled BoundedValue type")
    }

    operator fun compareTo(other: Int): Int =
        compareTo(Bounded(other))

}