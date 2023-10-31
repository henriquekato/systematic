package br.all.domain.shared.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NeverEmptyMutableSetTest {
    @Test
    fun `Should throw if attempting to create an NeverEmptyMutableSet from an empty source`() {
        assertThrows<IllegalArgumentException> { NeverEmptyMutableSet<Int>() }
    }
    
    @Test
    fun `Should keep at least one elements when clearing an NeverEmptyMutableSet`() {
        val sut = neverEmptyMutableSetOf(10, 25, 30, 88)
        sut.clear()
        assertEquals(1, sut.size)
    }
    
    @Test
    fun `Should keep the correct element when clearing an NeverEmptyMutableSet`() {
        val sut = neverEmptyMutableSetOf(10, 25, 30, 88)
        sut.clear(30)
        assertAll(
            { assertEquals(1, sut.size) },
            { assertEquals(30, sut.first()) }
        )
    }
}
