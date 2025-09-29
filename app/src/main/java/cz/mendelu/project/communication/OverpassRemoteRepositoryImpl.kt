package cz.mendelu.project.communication

import javax.inject.Inject

class OverpassRemoteRepositoryImpl @Inject constructor(private val overpassAPI: OverpassAPI) : IOverpassRemoteRepository {

    override suspend fun fetchGasStations(lat: Double, lon: Double): CommunicationResult<GasStationResponse> {
        return makeApiCall { overpassAPI.fetchGasStations(OverpassUtils.buildQuery(lat, lon)) }
    }
}
