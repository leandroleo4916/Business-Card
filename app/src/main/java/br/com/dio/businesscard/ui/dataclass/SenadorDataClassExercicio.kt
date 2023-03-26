package br.com.dio.businesscard.ui.dataclass

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class SenadorDataClassExercicio(
    @SerializedName("ListaParlamentarEmExercicio")
    val listaParlamentarEmExercicio: ListaParlamentarEmExercicio
)

data class ListaParlamentarEmExercicio (
    @SerializedName("@xmlns:xsi")
    val xmlnsXsi: String,

    @SerializedName("@xsi:noNamespaceSchemaLocation")
    val xsiNoNamespaceSchemaLocation: String,

    @SerializedName("Metadados")
    val metadados: Metadadoss,

    @SerializedName("Parlamentares")
    val parlamentares: Parlamentaress
)

data class Metadadoss (
    @SerializedName("Versao")
    val versao: String,

    @SerializedName("VersaoServico")
    val versaoServico: String,

    @SerialName("DataVersaoServico")
    val dataVersaoServico: String,

    @SerialName("DescricaoDataSet")
    val descricaoDataSet: String
)

data class Parlamentaress (
    @SerializedName("Parlamentar")
    val parlamentar: List<Parlamentarr>
)

data class Parlamentarr (
    @SerializedName("IdentificacaoParlamentar")
    val identificacaoParlamentar: IdentificacaoParlamentarr,

    @SerializedName("Mandato")
    val mandato: Mandatoo
)

data class IdentificacaoParlamentarr (
    @SerializedName("CodigoParlamentar")
    val codigoParlamentar: String,

    @SerializedName("CodigoPublicoNaLegAtual")
    val codigoPublicoNaLegAtual: String,

    @SerializedName("NomeParlamentar")
    val nomeParlamentar: String,

    @SerializedName("NomeCompletoParlamentar")
    val nomeCompletoParlamentar: String,

    @SerializedName("SexoParlamentar")
    val sexoParlamentar: String,

    @SerializedName("FormaTratamento")
    val formaTratamento: String,

    @SerializedName("UrlFotoParlamentar")
    val urlFotoParlamentar: String,

    @SerializedName("UrlPaginaParlamentar")
    val urlPaginaParlamentar: String,

    @SerializedName("UrlPaginaParticular")
    val urlPaginaParticular: String? = null,

    @SerializedName("EmailParlamentar")
    val emailParlamentar: String? = null,

    @SerializedName("Telefones")
    val telefones: TelefoneElement? = null,

    @SerializedName("SiglaPartidoParlamentar")
    val siglaPartidoParlamentar: String,

    @SerializedName("UfParlamentar")
    val ufParlamentar: String,

    @SerializedName("MembroMesa")
    val membroMesa: String,

    @SerializedName("MembroLideranca")
    val membroLideranca: String
)

data class TelefoneElement (
    @SerializedName("NumeroTelefone")
    val numeroTelefone: String,

    @SerializedName("OrdemPublicacao")
    val ordemPublicacao: String,

    @SerializedName("IndicadorFax")
    val indicadorFax: String
)

data class Mandatoo (
    @SerializedName("CodigoMandato")
    val codigoMandato: String,

    @SerializedName("UfParlamentar")
    val ufParlamentar: String,

    @SerializedName("PrimeiraLegislaturaDoMandato")
    val primeiraLegislaturaDoMandato: ALegislaturaDoMandatoo,

    @SerializedName("SegundaLegislaturaDoMandato")
    val segundaLegislaturaDoMandato: ALegislaturaDoMandatoo,

    @SerializedName("DescricaoParticipacao")
    val descricaoParticipacao: DescricaoParticipacao,

    @SerializedName("Suplentes")
    val suplentes: Suplentess,

    @SerializedName("Exercicios")
    val exercicios: Exercicioss,

    @SerializedName("Titular")
    val titular: Titularr? = null
)

@Serializable
enum class DescricaoParticipacao(val value: String) {
    The1ºSuplente("1º Suplente"),
    The2ºSuplente("2º Suplente"),
    Titular("Titular");

    companion object : KSerializer<DescricaoParticipacao> {
        override val descriptor: SerialDescriptor
            get() {
                return PrimitiveSerialDescriptor("quicktype.DescricaoParticipacao", PrimitiveKind.STRING)
            }
        override fun deserialize(decoder: Decoder): DescricaoParticipacao = when (val value = decoder.decodeString()) {
            "1º Suplente" -> The1ºSuplente
            "2º Suplente" -> The2ºSuplente
            "Titular"     -> Titular
            else          -> throw IllegalArgumentException("DescricaoParticipacao could not parse: $value")
        }
        override fun serialize(encoder: Encoder, value: DescricaoParticipacao) {
            return encoder.encodeString(value.value)
        }
    }
}

data class Exercicioss (
    @SerializedName("Exercicio")
    val exercicio: List<Exercicio>
)

data class Exercicio (
    @SerializedName("CodigoExercicio")
    val codigoExercicio: String,

    @SerializedName("DataInicio")
    val dataInicio: String,

    @SerializedName("DataFim")
    val dataFim: String? = null,

    @SerializedName("SiglaCausaAfastamento")
    val siglaCausaAfastamento: String? = null,

    @SerializedName("DescricaoCausaAfastamento")
    val descricaoCausaAfastamento: String? = null,

    @SerializedName("DataLeitura")
    val dataLeitura: String? = null
)

data class ALegislaturaDoMandatoo (
    @SerializedName("NumeroLegislatura")
    val numeroLegislatura: String,

    @SerializedName("DataInicio")
    val dataInicio: String,

    @SerializedName("DataFim")
    val dataFim: String
)

data class Suplentess (
    @SerializedName("Suplente")
    val suplente: List<Titular>
)

data class Titularr (
    @SerializedName("DescricaoParticipacao")
    val descricaoParticipacao: DescricaoParticipacao,

    @SerializedName("CodigoParlamentar")
    val codigoParlamentar: String,

    @SerializedName("NomeParlamentar")
    val nomeParlamentar: String
)

