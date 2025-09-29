package cz.mendelu.project.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import cz.mendelu.project.communication.GasStationItem


class CustomMapRenderer(
    val context: Context,
    val googleMap: GoogleMap,
    val clusterManager: ClusterManager<GasStationItem>
) : DefaultClusterRenderer<GasStationItem>(context, googleMap, clusterManager) {


    override fun shouldRenderAsCluster(cluster: Cluster<GasStationItem>): Boolean {
        return cluster.size > 2
    }


    /*override fun onBeforeClusterItemRendered(item: GasStationItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        // Set custom icon based on school type
        markerOptions.icon(item.getMarkerIcon(context))
    }*/

}