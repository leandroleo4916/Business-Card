package br.com.dio.businesscard.ui.dataclass

data class SenadorRanking(
    val id: String,
    val nome: String,
    val foto: String,
    var gasto: Int,
    val partido: String,
    val estado: String
)
