package com.sannorem.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.BarangayGroup

class AssetBarangayRepository(
    private val jsonProvider: () -> String
) : BarangayRepository {

    constructor(context: Context) : this({
        context.assets.open("barangays.json").bufferedReader().use { it.readText() }
    })

    private val gson = Gson()
    private var cachedBarangays: List<Barangay>? = null
    private val aliasMap = mutableMapOf<String, String>(
        "balaytigue" to "balaytigui",
        "muntingindang" to "munting-indan"
    )

    private data class BarangayDto(
        val id: String?,
        val name: String?,
        val group: String?,
        val latitude: Double?,
        val longitude: Double?,
        val source: String?,
        val aliases: List<String>?
    )

    override fun getBarangays(): List<Barangay> {
        cachedBarangays?.let { return it }

        return try {
            val json = jsonProvider()
            val type = object : TypeToken<List<BarangayDto>>() {}.type
            val dtos: List<BarangayDto>? = gson.fromJson(json, type)

            if (dtos.isNullOrEmpty()) {
                logError("AssetBarangayRepository", "Empty or null barangays list loaded")
                emptyList()
            } else {
                val barangays = dtos.mapNotNull { dto ->
                    val group = parseGroup(dto.group)
                    if (dto.id != null && dto.name != null && group != null) {
                        dto.aliases?.forEach { alias ->
                            aliasMap[normalize(alias)] = dto.id
                        }
                        aliasMap[normalize(dto.name)] = dto.id

                        Barangay(
                            id = dto.id,
                            name = dto.name,
                            group = group,
                            latitude = dto.latitude,
                            longitude = dto.longitude
                        )
                    } else {
                        logError("AssetBarangayRepository", "Malformed barangay entry skipped: $dto")
                        null
                    }
                }
                cachedBarangays = barangays
                barangays
            }
        } catch (e: Exception) {
            logError("AssetBarangayRepository", "Failed to load barangays from JSON", e)
            emptyList()
        }
    }

    override fun findByNameOrAlias(query: String): Barangay? {
        val barangays = getBarangays()
        val normalized = normalize(query)
        val targetId = aliasMap[normalized]
        if (targetId != null) {
            return barangays.firstOrNull { it.id == targetId }
        }
        return barangays.firstOrNull {
            normalize(it.name) == normalized ||
                    normalize(it.name).contains(normalized) ||
                    normalized.contains(normalize(it.name))
        }
    }

    private fun normalize(str: String): String {
        return str.lowercase()
            .replace("barangay", "")
            .replace("brgy", "")
            .replace("(", "")
            .replace(")", "")
            .replace("-", "")
            .replace(" ", "")
            .trim()
    }

    private fun parseGroup(groupStr: String?): BarangayGroup? {
        return when (groupStr?.uppercase()) {
            "POBLACION" -> BarangayGroup.POBLACION
            "RURAL_COASTAL", "RURAL & COASTAL", "RURAL_AND_COASTAL" -> BarangayGroup.RURAL_COASTAL
            else -> null
        }
    }

    private fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e(tag, message, throwable)
        } catch (_: Throwable) {
            System.err.println("[$tag] $message ${throwable?.message.orEmpty()}")
        }
    }
}
