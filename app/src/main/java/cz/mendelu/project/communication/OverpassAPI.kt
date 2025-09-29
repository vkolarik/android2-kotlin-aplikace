package cz.mendelu.project.communication

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST


interface OverpassAPI {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("api/interpreter")
    suspend fun fetchGasStations(@Field("data") query: String): Response<GasStationResponse>

}