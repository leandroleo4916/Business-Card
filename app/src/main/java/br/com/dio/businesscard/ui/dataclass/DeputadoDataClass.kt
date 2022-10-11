package br.com.dio.businesscard.ui.dataclass

data class Deputado (
    val id: Long,
    val nome: String,
    val tipoDoc: String,
    val urlFoto: String,
    val gasto: Int
)

