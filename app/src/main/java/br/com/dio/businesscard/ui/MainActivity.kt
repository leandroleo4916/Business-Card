package br.com.dio.businesscard.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.dio.businesscard.databinding.ActivityMainBinding
import br.com.dio.businesscard.ui.dataclass.Dado
import br.com.dio.businesscard.ui.dataclass.DadoDespesas
import br.com.dio.businesscard.ui.dataclass.Despesas
import br.com.dio.businesscard.ui.dataclass.NomeGastoTotal
import br.com.dio.businesscard.ui.remote.Retrofit
import br.com.dio.businesscard.ui.remote.ApiServiceIdDespesas
import br.com.dio.businesscard.ui.repository.ResultRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.Normalizer
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val getEncodeString = EncodeString()
    private val calculateTotal = CalculateDados()
    private var sizeTotal = 0
    private var anoSoma = 2015
    //----------------------------------//
    private val viewModel: DespesasViewModel by viewModel()
    private val viewModelCamara: CamaraViewModel by viewModel()
    private var listDeputado: ArrayList<Dado> = arrayListOf()
    private var listGastoPorDeputado: ArrayList<DadoDespesas> = arrayListOf()
    private var listNomeGasto: ArrayList<NomeGastoTotal> = arrayListOf()
    private var listNomeGastoRanking: ArrayList<NomeGastoTotal> = arrayListOf()
    private var idNome = ""
    private var nome = ""
    private var foto = ""
    private var partido = ""
    private var estado = ""
    private var numberNoteTotal = 0
    private var numberNoteAno = 0
    private var sizeCount = 0
    private var page = 1
    private var year = 2015
    private var id = ""

    var manutencao = 0
    var combustivel = 0
    var assinatura = 0
    var passagens = 0
    var divulgacao = 0
    var telefonia = 0
    var postais = 0
    var alimentacao = 0
    var hospedagem = 0
    var taxi = 0
    var locacao = 0
    var consultoria = 0
    var seguranca = 0
    var curso = 0
    var outros = 0

    var manutencaoT = 0
    var combustivelT = 0
    var assinaturaT = 0
    var passagensT = 0
    var divulgacaoT = 0
    var telefoniaT = 0
    var postaisT = 0
    var alimentacaoT = 0
    var hospedagemT = 0
    var taxiT = 0
    var locacaoT = 0
    var consultoriaT = 0
    var segurancaT = 0
    var cursoT = 0
    var outrosT = 0

    var totalGeralAno = 0
    var totalGeralSoma = 0
    var listNomePrint: ArrayList<String> = arrayListOf()
    var countDeputado = 0

    //-----------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpPermissions()

        insertListeners()
    }

    private fun setUpPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )
    }

    private fun insertListeners() {
        binding.run {
            textProcesso.setOnClickListener {
                binding.textProcesso.text = "Processando dados..."
                observerListDeputado()
            }
            textProcessoSen.setOnClickListener{
                binding.textProcessoSen.text = "Processando dados..."
            }
        }
    }

    private fun searchDoc() {

        val list: ArrayList<String> = arrayListOf()
        try {
            val arq = File(Environment.getExternalStorageDirectory(), "$anoSoma.txt")
            val br = BufferedReader(
                InputStreamReader(FileInputStream(arq), getEncodeString.getEncoding(arq))
            )

            br.forEachLine {
                val div = it.split("\";\"")
                list.add(if (div[0] == "") "Não foi informado" else div[0].split("\"")[1])
                list.add(if (div[1] == "") "Não foi informado" else div[1])
                list.add(if (div[2] == "") "Não foi informado" else div[2])
                list.add(if (div[3] == "") "Não foi informado" else div[3])
                list.add(if (div[4] == "") "Não foi informado" else div[4])
                list.add(if (div[5] == "") "Não foi informado" else div[5])
                list.add(if (div[6] == "") "Não foi informado" else div[6])
                list.add(if (div[7] == "") "Não foi informado" else div[7])
                list.add(if (div[8] == "") "Não foi informado" else div[8])
                list.add(if (div[9] == "") "Não foi informado" else div[9])
                list.add(if (div[10]== "") "Não foi informado" else div[10].split("\"")[0])

                sizeTotal += 1
                binding.textTotalProcesso.text = "$sizeTotal processados"
            }
            //download(list)

        } catch (e: java.lang.Exception) { }

    }

    private fun download(list: ArrayList<String>) {

        var listC: String
        var listD: ArrayList<String> = arrayListOf()
        val size = list.size
        var position = 0
        var name = ""
        var namePrint = ""

        var positionAno = 0
        var positionMes = 1
        var positionSen = 2
        var positionTip = 3
        var positionCnp = 4
        var positionFor = 5
        var positionDoc = 6
        var positionDat = 7
        var positionDet = 8
        var positionVal = 9
        var positionCod = 10

        var ano = ""
        var mes = ""
        var senador = ""
        var tipoDespesa = ""
        var cnpjCpf = ""
        var fornecedor = ""
        var documento = ""
        var data = ""
        var detalhamento = ""
        var valorReembolsado = ""
        var codDocumento = ""

        val anoV = """"ano""""
        val mesV = """"mes""""
        val senadorV = """"senador""""
        val tipoDespesaV = """"tipoDespesa""""
        val cnpjCpfV = """"cnpjCpf""""
        val fornecedorV = """"fornecedor""""
        val documentoV = """"documento""""
        val dataV = """"data""""
        val detalhamentoV = """"detalhamento""""
        val valorReembolsadoV = """"valorReembolsado""""
        val codDocumentoV = """"codDocumento""""

        list.forEach { i ->
            when (position) {
                positionAno -> {
                    ano = "\"$i\""
                    positionAno += 11
                    position += 1
                }
                positionMes -> {
                    mes = "\"$i\""
                    positionMes += 11
                    position += 1
                }
                positionSen -> {
                    senador = "\"$i\""
                    positionSen += 11
                    position += 1
                    if (name == "") {
                        name = i
                        namePrint = i
                    } else {
                        if (i == namePrint) {
                            name = i
                            namePrint = i
                        } else {
                            //rec(listD.toString(), name)
                            listD = arrayListOf()
                            name = i
                            namePrint = i
                        }
                    }
                }
                positionTip -> {
                    tipoDespesa = "\"$i\""
                    positionTip += 11
                    position += 1
                }
                positionCnp -> {
                    cnpjCpf = "\"$i\""
                    positionCnp += 11
                    position += 1
                }
                positionFor -> {
                    fornecedor = "\"$i\""
                    positionFor += 11
                    position += 1
                }
                positionDoc -> {
                    documento = "\"$i\""
                    positionDoc += 11
                    position += 1
                }
                positionDat -> {
                    data = "\"$i\""
                    positionDat += 11
                    position += 1
                }
                positionDet -> {
                    detalhamento = "\"$i\""
                    positionDet += 11
                    position += 1
                }
                positionVal -> {
                    valorReembolsado = "\"$i\""
                    positionVal += 11
                    position += 1
                }
                positionCod -> {
                    codDocumento = "\"$i\""
                    positionCod += 11
                    position += 1

                    listC = """{$anoV:$ano, $mesV:$mes, $senadorV:$senador, $tipoDespesaV:$tipoDespesa, 
                    $cnpjCpfV:$cnpjCpf, $fornecedorV:$fornecedor, $documentoV:$documento, 
                    $dataV:$data, $detalhamentoV:$detalhamento, $valorReembolsadoV:$valorReembolsado, 
                    $codDocumentoV:$codDocumento}"""

                    listD.add(listC)
                    if (position == size) {
                        //rec(listD.toString(), name)
                        anoSoma += 1
                        if (anoSoma != 2023){
                            //searchDoc()
                        }
                    }
                }
            }
        }
    }

    private fun rec(text: String, nome: String) {

        try {
            val codi = deleteAccent(nome)
            val arq = File(Environment.getExternalStorageDirectory(), "/$anoSoma/$codi")
            val fos = FileOutputStream(arq)
            fos.write("{\"gastosSenador\": $text}".toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) { }
    }

    private fun deleteAccent(str: String): String{
        var ret = ""
        val lower = str.lowercase()
        val nfdNormalizedString: String = Normalizer.normalize(lower, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val text = pattern.matcher(nfdNormalizedString).replaceAll("")
        val nome = text.split(" ")
        nome.forEach{ ret += it }
        return ret
    }

    // Busca lista de deputados
    private fun observerListDeputado() {
        viewModelCamara.searchData(ordenarPor = "nome").observe(this) {
            it?.let { result ->
                when (result) {
                    is ResultRequest.Success -> {
                        result.dado?.let { deputados ->
                            listDeputado = deputados.dados as ArrayList
                            sizeCount = listDeputado.size
                            getInfoDeputado()
                        }
                    }
                    is ResultRequest.Error -> {
                        observerListDeputado()
                        result.exception.message?.let { it -> }
                    }
                    is ResultRequest.ErrorConnection -> {
                        observerListDeputado()
                        result.exception.message?.let { it -> }
                    }
                }
            }
        }
    }

    // Pega info de cada deputado e faz busca dos gastos -> observer()
    private fun getInfoDeputado(){
        if (countDeputado <= sizeCount){
            id = listDeputado[countDeputado].id.toString()
            idNome = listDeputado[countDeputado].id.toString()
            nome = listDeputado[countDeputado].nome
            foto = listDeputado[countDeputado].urlFoto
            partido = listDeputado[countDeputado].siglaPartido
            estado = listDeputado[countDeputado].siglaUf
            observer()
        }
        else recDeputado()
    }

    // Busca gasto por deputado
    private fun observer(){

        val retrofit = Retrofit.createService(ApiServiceIdDespesas::class.java)
        val call: Call<Despesas> = retrofit.getDespesas(id, year.toString(), 100, page)

        call.enqueue(object: Callback<Despesas> {
            override fun onResponse(call: Call<Despesas>, despesas: Response<Despesas>){
                when (despesas.code()){
                    200 -> {
                        val despesa = despesas.body()
                        if (despesa != null){
                            listGastoPorDeputado += despesa.dados
                            val size = despesa.dados.size
                            numberNoteTotal += size
                            numberNoteAno += size

                            if (size >= 100) {
                                page += 1
                                observer()
                            }
                            else {
                                getValuePerNoteType()
                                page = 1
                            }
                        }
                    }
                    429 -> observer()
                    else -> {
                        Toast.makeText(application, despesas.message(), Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<Despesas>, t: Throwable) {
                Toast.makeText(application, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Pega valor de cada nota e soma total de gastos de todos os deputados nos respectivos anos
    // Calcula e adiciona numa lista os gastos de cada deputado
    private fun getValuePerNoteType() {

        var gastoPorDeputado = 0
        listGastoPorDeputado.forEach{

            if (it.valorDocumento.toString() != "" && it.valorDocumento > 0){
                val valor = it.valorDocumento.toInt()
                totalGeralAno += valor
                totalGeralSoma += valor
                gastoPorDeputado += valor

                when (it.tipoDespesa) {
                    "MANUTENÇÃO DE ESCRITÓRIO DE APOIO À ATIVIDADE PARLAMENTAR" -> {
                        manutencao += valor
                        manutencaoT += valor
                    }
                    "COMBUSTÍVEIS E LUBRIFICANTES." -> {
                        combustivel += valor
                        combustivelT += valor
                    }
                    "DIVULGAÇÃO DA ATIVIDADE PARLAMENTAR." -> {
                        divulgacao += valor
                        divulgacaoT += valor
                    }
                    "ASSINATURA DE PUBLICAÇÕES" -> {
                        assinatura += valor
                        assinaturaT += valor
                    }
                    "PASSAGEM AÉREA - REEMBOLSO" -> {
                        passagens += valor
                        passagensT += valor
                    }
                    "PASSAGEM AÉREA - SIGEPA" -> {
                        passagens += valor
                        passagensT += valor
                    }
                    "PASSAGEM AÉREA - RPA" -> {
                        passagens += valor
                        passagensT += valor
                    }
                    "PASSAGENS TERRESTRES, MARÍTIMAS OU FLUVIAIS" -> {
                        passagens += valor
                        passagensT += valor
                    }
                    "TELEFONIA" -> {
                        telefonia += valor
                        telefoniaT += valor
                    }
                    "SERVIÇOS POSTAIS" -> {
                        postais += valor
                        postaisT += valor
                    }
                    "FORNECIMENTO DE ALIMENTAÇÃO DO PARLAMENTAR" -> {
                        alimentacao += valor
                        alimentacaoT += valor
                    }
                    "HOSPEDAGEM ,EXCETO DO PARLAMENTAR NO DISTRITO FEDERAL." -> {
                        hospedagem += valor
                        hospedagemT += valor
                    }
                    "SERVIÇO DE TÁXI, PEDÁGIO E ESTACIONAMENTO" -> {
                        taxi += valor
                        taxiT += valor
                    }
                    "LOCAÇÃO OU FRETAMENTO DE VEÍCULOS AUTOMOTORES" -> {
                        locacao += valor
                        locacaoT += valor
                    }
                    "CONSULTORIAS, PESQUISAS E TRABALHOS TÉCNICOS." -> {
                        consultoria += valor
                        consultoriaT += valor
                    }
                    "SERVIÇO DE SEGURANÇA PRESTADO POR EMPRESA ESPECIALIZADA." -> {
                        seguranca += valor
                        segurancaT += valor
                    }
                    "PARTICIPAÇÃO EM CURSO, PALESTRA OU EVENTO SIMILAR" -> {
                        curso += valor
                        cursoT += valor
                    }
                    else -> {
                        outros += valor
                        outrosT += valor
                    }
                }
            }
        }
        listNomeGasto.add(NomeGastoTotal(id, nome, foto, gastoPorDeputado, partido, estado))

        if (year != 2015){
            listNomeGastoRanking[countDeputado].gasto += gastoPorDeputado
        }
        else {
            listNomeGastoRanking
                .add(NomeGastoTotal(id, nome, foto, gastoPorDeputado, partido, estado))
        }
        println(NomeGastoTotal(id, nome, foto, gastoPorDeputado, partido, estado))

        binding.run {
            textNomeEGasto.text = "$nome: $gastoPorDeputado"
            textTotalProcesso.text = "Total deputado: "+countDeputado.toString()
            textTotalGeral.text = "Total geral: "+totalGeralAno.toString()
            textAno.text = "Ano: "+year.toString()
        }
        listGastoPorDeputado = arrayListOf()
        countDeputado += 1
        getInfoDeputado()
    }

    private fun recDeputado() {

        val totalGeralV = """"totalGeral""""
        val notasGeral = """"totalNotas""""

        val manutencaoV = """"manutencao""""
        val combustivelV = """"combustivel""""
        val divulgacaoV = """"divulgacao""""
        val assinaturaV = """"assinatura""""
        val passagensV = """"passagens""""
        val telefoniaV = """"telefonia""""
        val postaisV = """"postais""""
        val alimentacaoV = """"alimentacao""""
        val hospedagemV = """"hospedagem"""""
        val taxiV = """"taxi""""
        val locacaoV = """"locacao""""
        val consultoriaV = """"consultoria""""
        val segurancaV = """"seguranca""""
        val cursoV = """"curso""""
        val outrosV = """"outros""""

        // Gera JSON gastos do ano
        try {
            val listTotal = """{"gastoAno${anoSoma}": {$totalGeralV:"$totalGeralAno", $notasGeral:"$numberNoteAno", 
             $manutencaoV:"$manutencao", $combustivelV:"$combustivel", $passagensV:"$passagens", 
             $assinaturaV:"$assinatura", $divulgacaoV:"$divulgacao", $telefoniaV:"$telefonia", 
             $postaisV:"$postais", $hospedagemV:"$hospedagem", $taxiV:"$taxi", $locacaoV:"$locacao",
             $consultoriaV:"$consultoria", $segurancaV:"$seguranca", $cursoV:"$curso", 
             $alimentacaoV:"$alimentacao", $outrosV:"$outros"}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/geral${anoSoma}")
            val fos = FileOutputStream(arq)
            fos.write(listTotal.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) { }

        // Gera JSON ranking do ano
        listNomeGasto.forEach {
            listNomePrint
                .add("""{"id":"${it.id}", "nome":"${it.nome}", "foto":"${it.foto}", "gasto":"${it.gasto}", "partido":"${it.partido}", "estado":"${it.estado}"}""".trimMargin())
        }

        try {
            val ranking = """{"ranking$anoSoma: $listNomePrint"}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/ranking$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(ranking.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) { }

        when(anoSoma){
            2022 -> {
                // Gera JSON Gastos de todos os anos
                val listSomaTotal = """{"gastoGeralSoma": {$totalGeralV:"$totalGeralSoma", $notasGeral:"$numberNoteTotal", 
                    $manutencaoV:"$manutencaoT", $combustivelV:"$combustivelT", $passagensV:"$passagensT",
                    $assinaturaV:"$assinaturaT", $divulgacaoV:"$divulgacaoT", $telefoniaV:"$telefoniaT", 
                    $postaisV:"$postaisT", $hospedagemV:"$hospedagemT", $taxiV:"$taxiT", $locacaoV:"$locacaoT",
                    $consultoriaV:"$consultoriaT", $segurancaV:"$segurancaT", $cursoV:"$cursoT", 
                    $alimentacaoV:"$alimentacaoT", $outrosV:"$outrosT"}"""

                try {
                    val geral = """{"total: $listSomaTotal"}"""
                    val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/geral")
                    val fos = FileOutputStream(arq)
                    fos.write(geral.toByteArray())
                    fos.flush()
                    fos.close()
                } catch (e: java.lang.Exception) { }
            }
            else -> {
                // Zera contatores para o ano seguinte
                manutencao = 0
                combustivel = 0
                assinatura = 0
                passagens = 0
                divulgacao = 0
                telefonia = 0
                postais = 0
                alimentacao = 0
                hospedagem = 0
                taxi = 0
                locacao = 0
                consultoria = 0
                seguranca = 0
                curso = 0
                outros = 0

                // inicia nova busca por gastos no ano seguinte 2015 -> 2016 -> ...
                anoSoma += 1
                countDeputado = 0
                totalGeralAno = 0
                year += 1
                listNomeGasto = arrayListOf()
                listNomePrint = arrayListOf()
                observer()
            }
        }
    }
}