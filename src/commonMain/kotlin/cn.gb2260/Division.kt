package cn.gb2260

data class Division(
    val code: String,
    val province: String,
    val prefecture: String?,
    val county: String?,
    val revisionEnum: Revisions
) {

    val revision: String = revisionEnum.code

    val name: String = county ?: prefecture ?: province

    val description: String =
        "$province${prefecture?.let { " $it" }.orEmpty()}${county?.let { " $it" }.orEmpty()}"

    override fun toString(): String = description
}
