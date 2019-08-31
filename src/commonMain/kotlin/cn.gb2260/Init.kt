package cn.gb2260

internal expect fun loadData(revision: Revisions): Iterable<Pair<String, String>>
