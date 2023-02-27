package br.com.dio.businesscard.ui.dataclass

import com.google.gson.annotations.SerializedName

data class SenadorLegislatura (
    @SerializedName("ListaParlamentarLegislatura")
    val listaParlamentarLegislatura: ListaParlamentarLegislatura
)

data class ListaParlamentarLegislatura (
    @SerializedName("@xmlns:xsi")
    val xmlnsXsi: String,

    @SerializedName("@xsi:noNamespaceSchemaLocation")
    val xsiNoNamespaceSchemaLocation: String,

    @SerializedName("Metadados")
    val metadados: Metadados,

    @SerializedName("Parlamentares")
    val parlamentares: Parlamentares
)

data class Metadados (
    @SerializedName("Versao")
    val versao: String,

    @SerializedName("VersaoServico")
    val versaoServico: String,

    @SerializedName("DataVersaoServico")
    val dataVersaoServico: String,

    @SerializedName("DescricaoDataSet")
    val descricaoDataSet: String
)


data class Parlamentares (
    @SerializedName("Parlamentar")
    val parlamentar: List<Parlamentar>
)

data class Parlamentar (
    @SerializedName("IdentificacaoParlamentar")
    val identificacaoParlamentar: IdentificacaoParlamentar,

    @SerializedName("Mandatos")
    val mandatos: Mandatos
)

data class IdentificacaoParlamentar (
    @SerializedName("CodigoParlamentar")
    val codigoParlamentar: String,

    @SerializedName("NomeParlamentar")
    val nomeParlamentar: String,

    @SerializedName("NomeCompletoParlamentar")
    val nomeCompletoParlamentar: String,

    @SerializedName("SexoParlamentar")
    val sexoParlamentar: String,

    @SerializedName("FormaTratamento")
    val formaTratamento: String,

    @SerializedName("SiglaPartidoParlamentar")
    var siglaPartidoParlamentar: String? = null,

    @SerializedName("CodigoPublicoNaLegAtual")
    var codigoPublicoNaLegAtual: String? = null,

    @SerializedName("UrlFotoParlamentar")
    var urlFotoParlamentar: String? = null,

    @SerializedName("UrlPaginaParlamentar")
    var urlPaginaParlamentar: String? = null,

    @SerializedName("UrlPaginaParticular")
    var urlPaginaParticular: String? = null,

    @SerializedName("EmailParlamentar")
    var emailParlamentar: String? = null,

    @SerializedName("UfParlamentar")
    var ufParlamentar: String?
)

data class Mandatos (
    @SerializedName("Mandato")
    val mandato: Mandato
)

data class Mandato (
    @SerializedName("CodigoMandato")
    val codigoMandato: String,

    @SerializedName("UfParlamentar")
    val ufParlamentar: String,

    @SerializedName("PrimeiraLegislaturaDoMandato")
    val primeiraLegislaturaDoMandato: ALegislaturaDoMandato,

    @SerializedName("SegundaLegislaturaDoMandato")
    val segundaLegislaturaDoMandato: ALegislaturaDoMandato,

    @SerializedName("DescricaoParticipacao")
    val descricaoParticipacao: String,

    @SerializedName("Titular")
    var titular: Titular? = null,

    @SerializedName("Suplentes")
    var suplentes: Suplentes? = null,

    @SerializedName("Exercicios")
    var exercicios: Exercicios? = null
)

data class Exercicios (
    @SerializedName("Exercicio")
    val exercicio: Any
)

data class ExercicioElement (
    @SerializedName("CodigoExercicio")
    val codigoExercicio: String,

    @SerializedName("DataInicio")
    val dataInicio: String,

    @SerializedName("DataFim")
    val dataFim: String,

    @SerializedName("SiglaCausaAfastamento")
    val siglaCausaAfastamento: String,

    @SerializedName("DescricaoCausaAfastamento")
    val descricaoCausaAfastamento: String,

    @SerializedName("DataLeitura")
    var dataLeitura: String? = null
)

data class ALegislaturaDoMandato (
    @SerializedName("NumeroLegislatura")
    val numeroLegislatura: String,

    @SerializedName("DataInicio")
    val dataInicio: String,

    @SerializedName("DataFim")
    val dataFim: String
)

data class Suplentes (
    @SerializedName("Suplente")
    val suplente: Any
)

data class Titular (
    @SerializedName("DescricaoParticipacao")
    val descricaoParticipacao: String,

    @SerializedName("CodigoParlamentar")
    val codigoParlamentar: String,

    @SerializedName("NomeParlamentar")
    val nomeParlamentar: String
)

