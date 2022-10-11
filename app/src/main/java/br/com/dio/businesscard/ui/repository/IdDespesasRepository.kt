package br.com.dio.businesscard.ui.repository

import br.com.dio.businesscard.ui.remote.ApiServiceIdDespesas

sealed class ResultDespesasRequest<out R> {
    data class Success<out T>(val dado: T?) : ResultDespesasRequest<T?>()
    data class Error(val exception: Exception) : ResultDespesasRequest<Nothing>()
    data class ErrorConnection(val exception: Exception) : ResultDespesasRequest<Nothing>()
}

class IdDespesasRepository(private val serviceApi: ApiServiceIdDespesas) {


}