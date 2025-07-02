package codes.reason.nbs3df.mod.screen

abstract class Element {

    abstract fun createToken(availableSize: AvailableSize) : ElementToken

}