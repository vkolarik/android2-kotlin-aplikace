package cz.mendelu.project.communication

import com.squareup.moshi.Json
import cz.mendelu.project.R

data class GasStationResponse(
    val version: String,
    val osm3s: Osm3s,
    val elements: List<GasStation>
)

fun GasStationResponse.toGasStationItems(): List<GasStationItem> {
    return elements.map { gasStation ->
        // Extracting the name, lat, lon, and fuel type
        val fuelTypes = mutableListOf<FuelType>()

        // Check for each fuel type in the tags and add to the list if available
        if (gasStation.tags?.fuel_diesel != null) {
            fuelTypes.add(FuelType.DIESEL)
        }
        if (gasStation.tags?.fuel_lpg != null) {
            fuelTypes.add(FuelType.LPG)
        }
        if (gasStation.tags?.fuel_octane_95 != null) {
            fuelTypes.add(FuelType.PETROL)
        }

        // Creating a GasStationItem for each gas station
        GasStationItem(
            name = gasStation.tags?.name ?: "Unknown",
            lat = gasStation.lat,
            lon = gasStation.lon,
            fuelTypes = fuelTypes.ifEmpty { listOf(FuelType.UNKNOWN) } // If no fuel type is found, use FuelType.UNKNOWN
        )
    }
}

data class Osm3s(
    val timestamp_osm_base: String,
    val copyright: String
)

data class GasStation(
    val type: String,
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Tags? = null  // Making the tags nullable to handle missing tags
)

data class Tags(
    val amenity: String? = null,
    val brand: String? = null,
    val name: String? = null,
    val opening_hours: String? = null,
    val compressed_air: String? = null,
    @Json(name = "fuel:diesel") val fuel_diesel: String? = null,
    @Json(name = "fuel:lpg") val fuel_lpg: String? = null,
    @Json(name = "fuel:octane_95") val fuel_octane_95: String? = null,
    val website: String? = null,
    val wheelchair: String? = null,
    val operator: String? = null
)

enum class FuelType(val displayName: Int, val dbInt: Int) {
    DIESEL(R.string.diesel, 1),
    LPG(R.string.lpg, 2),
    PETROL(R.string.petrol, 3),
    UNKNOWN(R.string.unknown, 4);

    companion object {
        fun fromInt(dbInt: Int): FuelType {
            return when (dbInt) {
                1 -> DIESEL
                2 -> LPG
                3 -> PETROL
                else -> { UNKNOWN }
            }
        }
    }
}