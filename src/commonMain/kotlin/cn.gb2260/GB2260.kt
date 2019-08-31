package cn.gb2260

interface GB2260 {

    companion object {
        val DEFAULT_REVISION = Revisions.V2014
        operator fun invoke(revision: Revisions = DEFAULT_REVISION): GB2260 = DefaultGB2260(revision)
    }

    val revision: Revisions
    val revisions: List<Revisions>
    val provinces: List<Division>
    operator fun get(code: String): Division?
    /**
     * @throws IllegalStateException When code is invalid
     */
    fun getValue(code: String): Division
    fun prefecture(provinceCode: String): List<Division>
    fun counties(prefectureCode: String): List<Division>
}