package com.sannorem.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch

class AssetBranchRepository(
    private val jsonProvider: () -> String
) : BranchRepository {

    constructor(context: Context) : this({
        context.assets.open("branches.json").bufferedReader().use { it.readText() }
    })

    private val gson = Gson()
    private var cachedBranches: List<RetailBranch>? = null

    private data class BranchDto(
        val id: String?,
        val brand: String?,
        val branchName: String?,
        val latitude: Double?,
        val longitude: Double?,
        val serviceRadiusKm: Double?,
        val plusCode: String?,
        val addressNote: String?,
        val hours: String?,
        val brgy: String?,
        val verified: Boolean?,
        val attrs: List<String>?,
        val strategy: String?
    )

    override fun getBranches(): List<RetailBranch> {
        cachedBranches?.let { return it }

        return try {
            val json = jsonProvider()
            val type = object : TypeToken<List<BranchDto>>() {}.type
            val dtos: List<BranchDto>? = gson.fromJson(json, type)

            if (dtos.isNullOrEmpty()) {
                logError("AssetBranchRepository", "Empty or null branches list loaded")
                emptyList()
            } else {
                val branches = dtos.mapNotNull { dto ->
                    val brand = parseBrand(dto.brand)
                    if (dto.id != null && brand != null && dto.branchName != null &&
                        dto.latitude != null && dto.longitude != null
                    ) {
                        RetailBranch(
                            id = dto.id,
                            brand = brand,
                            branchName = dto.branchName,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            serviceRadiusKm = dto.serviceRadiusKm ?: Brand.defaultRadiusKm(brand),
                            address = dto.addressNote ?: "",
                            hours = dto.hours ?: if (brand == Brand.SEVEN_ELEVEN) "24/7" else "7:00 AM - 9:00 PM",
                            barangayId = dto.brgy ?: "",
                            barangayName = dto.addressNote ?: "",
                            verified = dto.verified ?: true,
                            attributes = dto.attrs ?: emptyList(),
                            strategy = dto.strategy ?: ""
                        )
                    } else {
                        logError("AssetBranchRepository", "Malformed branch entry skipped: $dto")
                        null
                    }
                }
                cachedBranches = branches
                branches
            }
        } catch (e: Exception) {
            logError("AssetBranchRepository", "Failed to load branches from JSON", e)
            emptyList()
        }
    }

    private fun parseBrand(brandStr: String?): Brand? {
        return when (brandStr?.uppercase()) {
            "DALI" -> Brand.DALI
            "SEVEN_ELEVEN", "7-ELEVEN", "7_ELEVEN", "7ELEVEN" -> Brand.SEVEN_ELEVEN
            "ALFAMART" -> Brand.ALFAMART
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
