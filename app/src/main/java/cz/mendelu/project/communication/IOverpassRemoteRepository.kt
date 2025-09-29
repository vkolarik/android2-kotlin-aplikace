package cz.mendelu.project.communication

interface IOverpassRemoteRepository : IBaseRemoteRepository {

    suspend fun fetchGasStations(lat: Double, lon: Double): CommunicationResult<GasStationResponse>

}