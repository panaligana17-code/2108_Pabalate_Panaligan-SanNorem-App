package com.sannorem.app.data

import com.sannorem.app.domain.model.BarangayGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class BarangayRepositoryTest {

    private lateinit var repository: AssetBarangayRepository

    @Before
    fun setUp() {
        val candidates = listOf(
            File("src/main/assets/barangays.json"),
            File("app/src/main/assets/barangays.json")
        )
        val jsonFile = candidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("barangays.json file not found in test search paths")

        repository = AssetBarangayRepository { jsonFile.readText() }
    }

    @Test
    fun getBarangays_hasExactly42BarangaysWithUniqueIds() {
        val barangays = repository.getBarangays()

        assertEquals("There must be exactly 42 barangays in Nasugbu", 42, barangays.size)

        val uniqueIds = barangays.map { it.id }.toSet()
        assertEquals("All 42 barangays must have unique IDs", 42, uniqueIds.size)
    }

    @Test
    fun getBarangays_groupDistribution_12PoblacionAnd30RuralCoastal() {
        val barangays = repository.getBarangays()

        val poblacion = barangays.filter { it.group == BarangayGroup.POBLACION }
        val ruralCoastal = barangays.filter { it.group == BarangayGroup.RURAL_COASTAL }

        assertEquals(12, poblacion.size)
        assertEquals(30, ruralCoastal.size)
    }

    @Test
    fun getBarangays_doesNotContainBalokBalok() {
        val barangays = repository.getBarangays()

        val hasBalokBalok = barangays.any {
            it.name.contains("Balok", ignoreCase = true) || it.id.contains("balok", ignoreCase = true)
        }
        assertFalse("Balok-Balok is not an official barangay of Nasugbu and must not be present", hasBalokBalok)
    }

    @Test
    fun findByNameOrAlias_acceptsAliases() {
        // Balaytigue alias for Balaytigui
        val balaytigui = repository.findByNameOrAlias("Balaytigue")
        assertNotNull("Should find Balaytigui using alias 'Balaytigue'", balaytigui)
        assertEquals("balaytigui", balaytigui?.id)

        // Munting Indang alias for Munting Indan
        val muntingIndan = repository.findByNameOrAlias("Munting Indang")
        assertNotNull("Should find Munting Indan using alias 'Munting Indang'", muntingIndan)
        assertEquals("munting-indan", muntingIndan?.id)
    }

    @Test
    fun getBarangays_all42BarangaysHaveVerifiedCoordinates() {
        val barangays = repository.getBarangays()

        assertEquals(42, barangays.size)
        val validCoords = barangays.filter { it.latitude != null && it.longitude != null }
        assertEquals("All 42 barangays have verified coordinates in updated dataset", 42, validCoords.size)

        val maugat = barangays.first { it.id == "maugat" }
        assertEquals(14.0792, maugat.latitude!!, 0.0001)
        assertEquals(120.7605, maugat.longitude!!, 0.0001)
    }

    @Test
    fun malformedJson_doesNotCrash_returnsEmptyList() {
        val brokenRepo = AssetBarangayRepository { "{ invalid json content ]" }
        val result = brokenRepo.getBarangays()
        assertTrue(result.isEmpty())
    }
}
