package codes.reason.nbs3df.mod.screen.builder

typealias TemplateBody = ScopedElementBuilder.() -> Unit

data class Template(
    val body: TemplateBody
) {
    companion object {
        fun template(body: TemplateBody): Template = Template(body)
    }
}
