package br.com.dio.businesscard.ui.remote

import br.com.dio.businesscard.ui.dataclass.Despesas
import br.com.dio.businesscard.ui.dataclass.MainDataClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceMain {
    @GET("/api/v2/deputados?")
    suspend fun getDeputados(
        @Query("ordem") ordem: String,
        @Query("ordenarPor") ordenarPor: String
    ): Response<MainDataClass>
}

interface ApiServiceIdDespesas {
    @GET("/api/v2/deputados/{id}/despesas?")
    fun getDespesas(
        @Path("id") id: String,
        @Query("ano") ano: String,
        @Query("itens") itens: Int,
        @Query("pagina") pagina: Int,
        @Query("ordem") ordem: String = "ASC",
        @Query("ordenarPor") ordenarPor: String = "ano"
    ): Call<Despesas>
}