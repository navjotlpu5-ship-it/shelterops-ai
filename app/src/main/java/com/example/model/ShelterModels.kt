package com.example.model

enum class ShelterStatus(val label: String) {
    OPERATIONAL("Operational"),
    NEAR_FULL("Near Capacity (>85%)"),
    FULL("Full (100%)"),
    COMPROMISED("Compromised / Hazard Zone")
}

data class MedicalSupport(
    val hasDoctorOnSite: Boolean,
    val icuBedsCount: Int,
    val oxygenCylindersCount: Int,
    val firstAidKitsCount: Int,
    val ambulanceAssigned: Boolean
)

data class ShelterSupplies(
    val foodDaysRemaining: Int,
    val drinkingWaterLiters: Int,
    val powerBackupHours: Int,
    val isDisabilityAccessible: Boolean,
    val hasWomenChildSafeZone: Boolean,
    val sanitationUnits: Int
)

data class Shelter(
    val id: String,
    val name: String,
    val stateName: String,
    val districtName: String,
    val lat: Double,
    val lon: Double,
    val totalCapacity: Int,
    val currentOccupancy: Int,
    val status: ShelterStatus,
    val medical: MedicalSupport,
    val supplies: ShelterSupplies,
    val contactPerson: String,
    val phone: String,
    val elevationMeters: Double = 50.0,
    val isFloodSafeZone: Boolean = true
) {
    val availableBeds: Int
        get() = (totalCapacity - currentOccupancy).coerceAtLeast(0)

    val occupancyPercentage: Int
        get() = if (totalCapacity > 0) ((currentOccupancy.toFloat() / totalCapacity) * 100).toInt().coerceIn(0, 100) else 0

    val isAvailable: Boolean
        get() = status != ShelterStatus.COMPROMISED && availableBeds > 0
}
