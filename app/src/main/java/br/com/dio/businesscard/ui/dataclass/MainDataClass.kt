package br.com.dio.businesscard.ui.dataclass

data class MainDataClass (
    val dados: List<ListDeputados>
)

data class ListDeputados (
    val uri: String,
    val nome: String,
    val idLegislaturaInicial: Long,
    val idLegislaturaFinal: Long,
    val nomeCivil: String,
    val siglaSexo: String,
    val urlRedeSocial: List<String>,
    val urlWebsite: List<String>,
    val dataNascimento: String,
    val dataFalecimento: String,
    val ufNascimento: String,
    val municipioNascimento: String
)

data class DeputadoClass (
    val dados: Dados, 
    val links: List<Linkk>
)

data class Dados (
    val id: Long,
    val uri: String,
    val nomeCivil: String,
    val ultimoStatus: UltimoStatus,
    val cpf: String,
    val sexo: String,
    val urlWebsite: String?,
    val redeSocial: List<String>,
    val dataNascimento: String,
    val dataFalecimento: String?,
    val ufNascimento: String,
    val municipioNascimento: String,
    val escolaridade: String
)

data class UltimoStatus (
    val id: Long,
    val uri: String,
    val nome: String,
    val siglaPartido: String,
    val uriPartido: String,
    val siglaUf: String,
    val idLegislatura: Long,
    val urlFoto: String,
    val email: String?,
    val data: String,
    val nomeEleitoral: String,
    val gabinete: Gabinete,
    val situacao: String,
    val condicaoEleitoral: String,
    val descricaoStatus: String
)

data class Gabinete (
    val nome: String? = null,
    val predio: String? = null,
    val sala: String? = null,
    val andar: String? = null,
    val telefone: String? = null,
    val email: String? = null
)

data class Linkk (
    val rel: String,
    val href: String
)

