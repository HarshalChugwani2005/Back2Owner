package com.back2owner.app.domain.usecase

import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.ItemCategory
import com.back2owner.app.data.repository.ItemRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the core UseCases, specifically focusing on data retrieval and business rules.
 * Uses MockK to isolate the business logic from repository implementations.
 */
class UseCaseTest {

    private val itemRepository = mockk<ItemRepository>()
    private val getLostItemsUseCase = GetLostItemsUseCase(itemRepository)

    @Test
    fun testGetLostItemsUseCase_returnsSuccess_withCorrectItems() = runTest {
        // Arrange
        val expectedItems = listOf(
            Item(title = "Item 1", itemType = "lost"),
            Item(title = "Item 2", itemType = "lost")
        )
        coEvery { itemRepository.getLostItems(any()) } returns Result.success(expectedItems)

        // Act
        val result = getLostItemsUseCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedItems, result.getOrNull())
    }

    @Test
    fun testGetLostItemsUseCase_withCategoryFilter_callsRepositoryWithCorrectFilter() = runTest {
        // Arrange
        val category = ItemCategory.Electronics
        val expectedItems = listOf(Item(title = "Electronics Item", category = category.name))
        coEvery { 
            itemRepository.getLostItems(match { it["category"] == category.name }) 
        } returns Result.success(expectedItems)

        // Act
        val result = getLostItemsUseCase(category)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedItems, result.getOrNull())
    }
}
