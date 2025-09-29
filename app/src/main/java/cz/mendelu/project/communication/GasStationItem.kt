package cz.mendelu.project.communication

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable

data class GasStationItem (
    val name: String,
    val lat: Double,
    val lon: Double,
    val fuelTypes: List<FuelType>
) : Serializable, ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return name
    }

    override fun getZIndex(): Float {
        return 0.0f
    }

    /*fun getMarkerIcon(context: Context): BitmapDescriptor {
        val iconResId = when (type) {
            "basic" -> R.drawable.primary
            "middle.xml" -> R.drawable.middle
            "university" -> R.drawable.university
            else -> R.drawable.other
        }
        return vectorToBitmap(context, iconResId)
    }

    fun vectorToBitmap(context: Context, vectorResId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, vectorResId) as VectorDrawable
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        // Create a Bitmap with the required size
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set the bounds for the drawable and draw it on the canvas
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // Convert the Bitmap to BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }*/

}
