package cn.gb2260

open class DefaultGB2260(final override val revision: Revisions) : GB2260 {

    companion object {
        private const val OPTIONAL_SEGMENT = "00"
        private val PROVINCE_REGEX = "^(\\d{2})".toRegex()
        private val PREFECTURE_REGEX = "^\\d{2}(\\d{2})".toRegex()
        private val COUNTY_REGEX = "^\\d{4}(\\d{2})".toRegex()
    }

    private data class Node(val fullCode: String, val name: String, val nest: Map<String, Node>)

    private val divisionTemplate = Division("", "", null, null, revision)

    private val data: Map<String, Node> by lazy {
        mutableMapOf<String, Node>().apply {

            loadData(revision).forEach { (code, name) ->

                val province =
                    code.province
                        ?.let { getOrPut(it) { Node(code, name, mutableMapOf()) } }
                        ?: return@forEach

                val prefecture =
                    code.prefecture
                        ?.let { prefectureCode ->
                            (province.nest as MutableMap).getOrPut(prefectureCode) { Node(code, name, mutableMapOf()) }
                        } ?: return@forEach

                code.county?.let { countyCode ->
                    (prefecture.nest as MutableMap)[countyCode] = Node(code, name, emptyMap())
                }
            }
        }
    }

    override val revisions: List<Revisions> get() = Revisions.values

    override val provinces: List<Division> by lazy {
        data.map { (_, province) -> divisionTemplate.copy(code = province.fullCode, province = province.name) }
    }

    override fun get(code: String): Division? = runCatching { getValue(code) }.getOrNull()

    override fun getValue(code: String): Division =
        countyNode(code).let { (province, prefecture, county) ->

            prefecture.checkPrefectureValid(code)
            county.checkCountyValid(code)

            divisionTemplate.copy(
                code = (county ?: prefecture ?: province).fullCode,
                province = province.name,
                prefecture = prefecture?.name,
                county = county?.name
            )
        }

    override fun prefecture(provinceCode: String): List<Division> =
        provinceNode(provinceCode).let { province ->
            province.nest.map { (_, prefecture) ->
                divisionTemplate.copy(
                    code = prefecture.fullCode,
                    province = province.name,
                    prefecture = prefecture.name
                )
            }
        }

    override fun counties(prefectureCode: String): List<Division> =
        prefectureNode(prefectureCode).let { (province, prefecture) ->
            prefecture?.nest?.map { (_, county) ->
                divisionTemplate.copy(
                    code = county.fullCode,
                    province = province.name,
                    prefecture = prefecture.name,
                    county = county.name
                )
            }.orEmpty()
        }

    private fun provinceNode(provinceCode: String): Node =
        data[provinceCode.province?.takeIfValidCodeSegment()] ?: provinceCodeError()

    private fun prefectureNode(prefectureCode: String): Pair<Node, Node?> =
        provinceNode(prefectureCode).let { it to it.nest[prefectureCode.prefecture?.takeIfValidCodeSegment()] }

    private fun countyNode(code: String): Triple<Node, Node?, Node?> =
        prefectureNode(code).let {
            Triple(
                it.first,
                it.second,
                it.second?.nest?.get(code.county?.takeIfValidCodeSegment())
            )
        }

    private val String.province: String?
        get() = PROVINCE_REGEX.find(this)?.groupValues?.getOrNull(1)

    private val String.prefecture: String?
        get() = PREFECTURE_REGEX.find(this)?.groupValues?.getOrNull(1)

    private val String.county: String?
        get() = COUNTY_REGEX.find(this)?.groupValues?.getOrNull(1)

    private fun String.takeIfValidCodeSegment(): String? = takeIf { it != OPTIONAL_SEGMENT }

    private fun Node?.checkPrefectureValid(code: String) {
        if (this == null && code.count() >= 4 && code.prefecture != OPTIONAL_SEGMENT) {
            prefectureCodeError()
        }
    }

    private fun Node?.checkCountyValid(code: String) {
        if (this == null && code.count() >= 6 && code.county != OPTIONAL_SEGMENT) {
            countyCodeError()
        }
    }

    private fun provinceCodeError(): Nothing = error("Invalid province code")
    private fun prefectureCodeError(): Nothing = error("Invalid prefecture code")
    private fun countyCodeError(): Nothing = error("Invalid county code")
}