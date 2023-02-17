package br.com.dio.businesscard.ui.repository

import androidx.lifecycle.liveData
import br.com.dio.businesscard.ui.remote.ApiServiceDeputadoMain
import br.com.dio.businesscard.ui.remote.ApiServiceMain
import java.net.ConnectException

sealed class ResultRequest<out R> {
    data class Success<out T>(val dado: T?) : ResultRequest<T?>()
    data class Error(val exception: Exception) : ResultRequest<Nothing>()
    data class ErrorConnection(val exception: Exception) : ResultRequest<Nothing>()
}

class SearchRepository(private val serviceApi: ApiServiceMain) {

    fun searchData() = liveData {
        try {
            val request = serviceApi.getDeputados()
            if(request.isSuccessful){
                emit(ResultRequest.Success(dado = request.body()))
            } else {
                emit(ResultRequest.Error(exception = Exception("Não foi possível conectar!")))
            }
        } catch (e: ConnectException) {
            emit(ResultRequest.ErrorConnection(exception = Exception("Falha na comunicação com API")))
        }
        catch (e: Exception) {
            emit(ResultRequest.Error(exception = e))
        }
    }
}