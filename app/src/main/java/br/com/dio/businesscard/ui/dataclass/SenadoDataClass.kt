package br.com.dio.businesscard.ui.dataclass

typealias ListSenado = Array<SenadoDataClass>

data class SenadoDataClass(
    val id: Long,
    val tipoDocumento: Long,
    val ano: Long,
    val mes: Long,
    val nomeSenador: String,
    val tipoDespesa: String,
    val cpfCnpj: String,
    val fornecedor: String,
    val documento: String?,
    val data: String,
    val detalhamento: String?,
    val valorReembolsado: Double
)