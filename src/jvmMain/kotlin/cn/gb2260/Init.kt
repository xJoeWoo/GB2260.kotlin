package cn.gb2260

import java.io.File

internal actual fun loadData(revision: Revisions): Iterable<Pair<String, String>> =
    File(object {}.javaClass.getResource("/GB2260/GB2260-${revision.code}.txt").file)
        .useLines { it.map { it.split('\t').let { it[0] to it[1] } }.toList() }
