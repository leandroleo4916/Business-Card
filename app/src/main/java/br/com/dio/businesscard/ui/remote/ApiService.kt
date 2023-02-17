package br.com.dio.businesscard.ui.remote

import br.com.dio.businesscard.ui.dataclass.DeputadoClass
import br.com.dio.businesscard.ui.dataclass.Despesas
import br.com.dio.businesscard.ui.dataclass.MainDataClass
import br.com.dio.businesscard.ui.dataclass.SenadoDataClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceDeputadoMain {
    @GET("/api/v2/deputados/{id}")
    fun getDeputados(
        @Path("id") id: String
    ): Call<DeputadoClass>
}

interface ApiServiceSenado {
    @GET("https://adm.senado.gov.br/adm-dadosabertos/api/v1/senadores/despesas_ceaps/{ano}")
    fun getDataSenado(
        @Path("ano") ano: String
    ): Call<SenadoDataClass>
}

interface ApiServiceMain {
    @GET("/arquivos/deputados/json/deputados.json")
    suspend fun getDeputados(): Response<MainDataClass>
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