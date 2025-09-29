package cz.mendelu.project.communication

import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface IBaseRemoteRepository {

    fun <T : Any> handleResponse(call: Response<T>): CommunicationResult<T> {

            if (call.isSuccessful){
                call.body()?.let {
                    return CommunicationResult.Success(it)
                }?:run {
                    return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
                }
            } else {
                return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
            }

    }


    suspend fun <T : Any> makeApiCall(apiCall: suspend () -> Response<T>): CommunicationResult<T> {
        try {
            val call: Response<T> = apiCall()
            if (call.isSuccessful){
                call.body()?.let {
                    return CommunicationResult.Success(it)
                }?:run {
                    return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
                }
            } else {
                return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
            }
        } catch (ex: UnknownHostException){
            return CommunicationResult.Exception(ex)
        } catch (ex: SocketTimeoutException){
            return CommunicationResult.ConnectionError()
        } catch (ex: Exception) {
            return CommunicationResult.Exception(ex)
        }
    }

}