package cn.gb2260

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class GB2260Test {

    val gb2260 = GB2260()
//    val gb2260 = GB2260(Revisions.V2003)

    @Test
    fun get() {
        assertEquals(gb2260["44"].code, "440000")
        assertEquals(gb2260["4412"].code, "441200")
        assertEquals(gb2260["441202"].code, "441202")

        assertEquals(gb2260["44"].name, "广东省")
        assertEquals(gb2260["4412"].name, "肇庆市")
        assertEquals(gb2260["441202"].name, "端州区")

        assertEquals(gb2260["441202"].province, "广东省")
        assertEquals(gb2260["441202"].prefecture, "肇庆市")
        assertEquals(gb2260["441202"].county, "端州区")
        assertEquals(gb2260["441202"].description, "广东省 肇庆市 端州区")

        assertEquals(gb2260["440000"].province, "广东省")
        assertEquals(gb2260["440000"].prefecture, null)
        assertEquals(gb2260["440000"].county, null)
    }

    @Test
    fun province() {
        assertTrue(gb2260.provinces.count() > 0)
        assertTrue(gb2260.provinces.any { it.name == "广东省" })
        assertTrue(gb2260.provinces.none { it.name == "肇庆市" })
    }

    @Test
    fun prefecture() {
        assertTrue(gb2260.prefecture("44").count() > 0)
        assertTrue(gb2260.prefecture("4412").count() > 0)
        assertTrue(gb2260.prefecture("441202").count() > 0)
        assertTrue(gb2260.prefecture("44").any { it.name == "肇庆市" })
        assertTrue(gb2260.prefecture("43").none { it.name == "肇庆市" })
    }

    @Test
    fun county() {
        assertTrue(gb2260.counties("4412").count() > 0)
        assertTrue(gb2260.counties("4412").any { it.name == "端州区" })
        assertTrue(gb2260.counties("441201").any { it.name == "端州区" })
        assertTrue(gb2260.counties("4312").none { it.name == "端州区" })
    }

    @Test
    fun errors() {
        assertFails { gb2260.prefecture("999999") }
        assertFails { gb2260.counties("999999") }
        assertFails { gb2260["999999"] }
        assertFails { gb2260["449999"] }
        assertFails { gb2260["440299"] }
        assertFails { gb2260["44abcd"] }
        assertFails { gb2260["44cd"] }
        assertFails { gb2260["abcd?"] }
    }
}