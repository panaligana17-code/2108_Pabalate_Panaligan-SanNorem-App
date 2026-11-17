package com.sannorem.app.data

import com.sannorem.app.domain.model.Brand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AssetBranchRepositoryTest {

    private lateinit var repository: AssetBranchRepository

    @Before
    fun setUp() {
        val candidates = listOf(
            File("src/main/assets/branches.json"),
            File("app/src/main/assets/branches.json")
        )
        val jsonFile = candidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("branches.json file not found in test search paths")

        repository = AssetBranchRepository { jsonFile.readText() }
    }

    @Test
    fun getBranches_hasExactly31BranchesWithUniqueIds() {
        val branches = repository.getBranches()

        assertEquals("There must be exactly 31 branches in the dataset", 31, branches.size)

        val uniqueIds = branches.map { it.id }.toSet()
        assertEquals("All 31 branches must have unique IDs", 31, uniqueIds.size)
    }

    @Test
    fun getBranches_brandCounts_matchSpecifications() {
        val branches = repository.getBranches()

        val daliCount = branches.count { it.brand == Brand.DALI }
        val sevenElevenCount = branches.count { it.brand == Brand.SEVEN_ELEVEN }
        val alfamartCount = branches.count { it.brand == Brand.ALFAMART }

        assertEquals(11, daliCount)
        assertEquals(4, sevenElevenCount)
        assertEquals(16, alfamartCount)
    }

    @Test
    fun getBranches_serviceRadii_matchDefaults() {
        val branches = repository.getBranches()

        branches.forEach { branch ->
            when (branch.brand) {
                Brand.DALI -> assertEquals(1.0, branch.serviceRadiusKm, 0.001)
                Brand.SEVEN_ELEVEN -> assertEquals(0.6, branch.serviceRadiusKm, 0.001)
                Brand.ALFAMART -> assertEquals(0.5, branch.serviceRadiusKm, 0.001)
            }
        }
    }

    @Test
    fun malformedJson_doesNotCrash_returnsEmptyList() {
        val brokenRepo = AssetBranchRepository { "{ not a json list }" }
        val result = brokenRepo.getBranches()
        assertTrue(result.isEmpty())
    }
}
