package br.com.dio.businesscard.ui.dataclass

data class GastosDataClass(
    val gastos: List<GastosSenado>
)

data class GastosSenado (
    val ano: String,
    val mes: String,
    val senador: String,
    val tipoDespesa: String,
    val cnpjCpf: String,
    val fornecedor: String,
    val documento: String,
    val data: String,
    val detalhamento: String,
    val valorReembolsado: String,
    val codDocumento: String
)


