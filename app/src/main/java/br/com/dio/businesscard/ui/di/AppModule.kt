package br.com.dio.businesscard.ui.di

import br.com.dio.businesscard.ui.*
import br.com.dio.businesscard.ui.remote.*
import br.com.dio.businesscard.ui.repository.IdDespesasRepository
import br.com.dio.businesscard.ui.repository.SearchRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
        single<Retrofit> {
                Retrofit.Builder()
                        .baseUrl("https://dadosabertos.camara.leg.br")
                        .client(get())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        }
        single {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build()
        }
        single<ApiServiceMain> {
                get<Retrofit>().create(ApiServiceMain::class.java)
        }
        single<ApiServiceIdDespesas> {
                get<Retrofit>().create(ApiServiceIdDespesas::class.java)
        }
        single<ApiServiceDeputadoMain> {
                get<Retrofit>().create(ApiServiceDeputadoMain::class.java)
        }
        single<ApiServiceSenado> {
                get<Retrofit>().create(ApiServiceSenado::class.java)
        }
        single<ApiServiceSenadores> {
                get<Retrofit>().create(ApiServiceSenadores::class.java)
        }
}

val viewModelModule = module { viewModel { CamaraViewModel(get()) } }
val viewModelDespesas = module { viewModel { DespesasViewModel(get()) } }

val repositorySearch = module { single { SearchRepository(get()) } }
val repositoryDespesasDeputado = module { single { IdDespesasRepository(get()) } }

val appModules = listOf( retrofitModule, viewModelModule, repositorySearch, viewModelDespesas,
        repositoryDespesasDeputado
)