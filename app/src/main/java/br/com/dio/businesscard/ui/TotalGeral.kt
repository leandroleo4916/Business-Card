package br.com.dio.businesscard.ui

data class TotalGeral(
    val total: String,
    val totalNotas: String,
    val ano2015: GastoAno?,
    val ano2016: GastoAno?,
    val ano2017: GastoAno?,
    val ano2018: GastoAno?,
    val ano2019: GastoAno?,
    val ano2020: GastoAno?,
    val ano2021: GastoAno?,
    val ano2022: GastoAno?,
    val aluguel: String,
    val divulgacao: String,
    val passagens: String,
    val contratacao: String,
    val aquisicao: String,
    val outros: String,
    val listNome: List<NomeGastoTotal>
)
