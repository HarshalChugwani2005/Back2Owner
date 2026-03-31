package com.back2owner.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the ItemCategory sealed class, verifying its factory method and collection of all categories.
 */
class ItemCategoryTest {

    @Test
    fun testFromString_withValidInputs_returnsCorrectCategory() {
        assertEquals(ItemCategory.Electronics, ItemCategory.fromString("electronics"))
        assertEquals(ItemCategory.Documents, ItemCategory.fromString("documents"))
        assertEquals(ItemCategory.Stationery, ItemCategory.fromString("stationery"))
        assertEquals(ItemCategory.Personal, ItemCategory.fromString("personal"))
        assertEquals(ItemCategory.Clothing, ItemCategory.fromString("clothing"))
        assertEquals(ItemCategory.Other, ItemCategory.fromString("other"))
    }

    @Test
    fun testFromString_withInvalidInput_returnsOtherCategory() {
        assertEquals(ItemCategory.Other, ItemCategory.fromString("unknown_category"))
        assertEquals(ItemCategory.Other, ItemCategory.fromString(""))
    }

    @Test
    fun testGetAllCategories_returnsFullList() {
        val categories = ItemCategory.getAllCategories()
        assertEquals(6, categories.size)
        assert(categories.contains(ItemCategory.Electronics))
        assert(categories.contains(ItemCategory.Documents))
        assert(categories.contains(ItemCategory.Stationery))
        assert(categories.contains(ItemCategory.Personal))
        assert(categories.contains(ItemCategory.Clothing))
        assert(categories.contains(ItemCategory.Other))
    }
}
