package cz.mendelu.project.communication

class OverpassUtils {
    companion object {
        // Static-like function in Kotlin (inside companion object)
        fun buildQuery(lat: Double, lon: Double): String {
            return """
                [out:json];
                node["amenity"="fuel"](around:5000,$lat,$lon);
                out;
            """.trimIndent()
        }
    }
}
