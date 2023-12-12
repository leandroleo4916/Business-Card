package br.com.dio.businesscard.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.dio.businesscard.databinding.ActivityMainBinding
import br.com.dio.businesscard.ui.dataclass.*
import br.com.dio.businesscard.ui.model.CamaraViewModel
import br.com.dio.businesscard.ui.remote.ApiServiceDeputadoMain
import br.com.dio.businesscard.ui.remote.ApiServiceIdDespesas
import br.com.dio.businesscard.ui.remote.Retrofit
import br.com.dio.businesscard.ui.repository.ResultRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var anoSoma = 2011

    private val viewModelCamara: CamaraViewModel by viewModel()
    private var listDeputado: ArrayList<ListDeputados> = arrayListOf()
    private var listGastoPorDeputado: ArrayList<DadoDespesas> = arrayListOf()
    private var listNomeGastoAnoRanking: ArrayList<NomeGastoTotal> = arrayListOf()
    private var listNomeGastoRankingGeral: ArrayList<NomeGastoTotal> = arrayListOf()
    private var idNome = ""
    private var nome = ""
    private var foto = ""
    private var partido = ""
    private var estado = ""
    private var numberNoteTotal = 0
    private var numberNoteAno = 0
    private var page = 1
    private var year = 2011
    private var idV = ""

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
    var totalGeralSoma: Long = 0
    var listNomePrint: ArrayList<String> = arrayListOf()
    var listNomePrintGeral: ArrayList<String> = arrayListOf()
    var countDeputado = 0
    var tentativa = 0

    //-----------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpPermissions()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
            textProcessoSen.setOnClickListener {
                val intent = Intent(baseContext.applicationContext, SenadoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Busca lista de deputados
    private fun observerListDeputado() {
        viewModelCamara.searchData().observe(this) {
            it?.let { result ->
                when (result) {
                    is ResultRequest.Success -> {
                        result.dado?.let { deputados ->
                            listDeputado = deputados.dados as ArrayList
                            println("Baixou lista de deputados")
                            getInfoDeputado()
                        }
                    }
                    is ResultRequest.Error -> {
                        observerListDeputado()
                        result.exception.message?.let { it ->
                            binding.textErro.text = "Erro em API - Lista de Deputados"
                            println(it)
                        }
                    }
                    is ResultRequest.ErrorConnection -> {
                        observerListDeputado()
                        result.exception.message?.let { it ->
                            binding.textErro.text = "Erro em API - Lista de Deputados"
                            println(it)
                        }
                    }
                }
            }
        }
    }

    private fun getInfoDeputado() {
        if (countDeputado < 2300){
            listDeputado[countDeputado].run {
                if (idLegislaturaInicial >= 54 || idLegislaturaFinal >= 54){
                    println("Processando dados ${nome} - $anoSoma")
                    val idDep = uri.split("deputados/")
                    observerDeputado(idDep[1])
                    binding.textTotalProcesso.text = "Total deputado: " + countDeputado.toString()
                    countDeputado += 1
                }
                else {
                    binding.textTotalProcesso.text = "Total deputado: " + countDeputado.toString()
                    countDeputado += 1
                    getInfoDeputado()
                }
            }
        }else recDeputado()
    }

    // Pega info de cada deputado e faz busca dos gastos -> observer()
    private fun observerDeputado(idDep: String) {

        val retrofit = Retrofit.createService(ApiServiceDeputadoMain::class.java)
        val call: Call<DeputadoClass> = retrofit.getDeputados(idDep)

        call.enqueue(object : Callback<DeputadoClass> {
            override fun onResponse(call: Call<DeputadoClass>, despesas: Response<DeputadoClass>) {
                when (despesas.code()) {
                    200 -> {
                        despesas.body()?.dados?.run {
                            idV = this.id.toString()
                            idNome = this.id.toString()
                            nome = this.nomeCivil
                            foto = this.ultimoStatus.urlFoto
                            partido = this.ultimoStatus.siglaPartido
                            estado = this.ultimoStatus.siglaUf
                            binding.textEmProcesso.text = "Em processamento: $nome"
                            observer()
                        }
                    }
                    429 -> observerDeputado(idDep)
                    else -> observerDeputado(idDep)
                }
            }
            override fun onFailure(call: Call<DeputadoClass>, t: Throwable) {
                observerDeputado(idDep)
            }
        })
    }

    // Busca gasto por deputado
    private fun observer() {

        val retrofit = Retrofit.createService(ApiServiceIdDespesas::class.java)
        val call: Call<Despesas> = retrofit.getDespesas(idV, year.toString(), 100, page)

        call.enqueue(object : Callback<Despesas> {
            override fun onResponse(call: Call<Despesas>, despesas: Response<Despesas>) {
                when (despesas.code()) {
                    200 -> {
                        val despesa = despesas.body()
                        if (despesa != null) {
                            listGastoPorDeputado += despesa.dados
                            val size = despesa.dados.size
                            numberNoteTotal += size
                            numberNoteAno += size

                            if (size >= 100) {
                                page += 1
                                observer()
                            } else {
                                if (size != 0){
                                    page = 1
                                    getValuePerNoteType()
                                }
                                else {
                                    if (page != 1){
                                        page = 1
                                        getValuePerNoteType()
                                    }
                                    else {
                                        page = 1
                                        getInfoDeputado()
                                    }
                                }
                            }
                        }
                        else getInfoDeputado()
                    }
                    429 -> observer()
                    else -> {
                        if (tentativa <= 1) {
                            observer()
                            tentativa = +1
                        }
                        println(despesas.message())
                        binding.textErro.text = "Erro em API - Gastos"
                        Toast.makeText(application, despesas.message(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Despesas>, t: Throwable) {
                if (tentativa <= 1) {
                    observer()
                    tentativa = +1
                }
                println(t.message)
                binding.textErro.text = "Erro em API - Gastos"
                Toast.makeText(application, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Pega valor de cada nota e soma total de gastos de todos os deputados nos respectivos anos
    // Calcula e adiciona numa lista os gastos de cada deputado
    private fun getValuePerNoteType() {

        var gastoPorDeputado = 0
        for (it in listGastoPorDeputado) {

            if (it.valorDocumento.toString() != "" && it.valorDocumento > 0) {
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
        listNomeGastoAnoRanking.add(NomeGastoTotal(idV, nome, foto, gastoPorDeputado, partido, estado))
        listNomePrint.add("""{"id":"$idV", "nome":"$nome", "foto":"$foto", "gasto":"$gastoPorDeputado", "partido":"$partido", "estado":"$estado"}""".trimMargin())
        println("""{"id":"$idV", "nome":"$nome", "foto":"$foto", "gasto":"$gastoPorDeputado", "partido":"$partido", "estado":"$estado"}""".trimMargin())

        var confirm = false
        for (it in listNomeGastoRankingGeral) {
            if (it.id == idV) {
                it.gasto += gastoPorDeputado
                confirm = true
                break
            }
        }
        if (!confirm) {
            listNomeGastoRankingGeral.add(NomeGastoTotal(idV, nome, foto, gastoPorDeputado, partido, estado))
        }
        println(NomeGastoTotal(idV, nome, foto, gastoPorDeputado, partido, estado))
        println("Fim do processo de $nome")
        println("-------------------------------------------------------------------------------")

        binding.run {
            textNomeEGasto.text = "$nome: $gastoPorDeputado"
            textTotalProcesso.text = "Total deputado: " + countDeputado.toString()
            textTotalGeral.text = "Total geral: " + totalGeralAno.toString()
            textAno.text = "Ano: " + year.toString()
        }
        listGastoPorDeputado = arrayListOf()
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
        val hospedagemV = """"hospedagem""""
        val taxiV = """"taxi""""
        val locacaoV = """"locacao""""
        val consultoriaV = """"consultoria""""
        val segurancaV = """"seguranca""""
        val cursoV = """"curso""""
        val outrosV = """"outros""""

        // Gera JSON gastos do ano
        try {
            val listTotal =
                """{"gastoGeral": {$totalGeralV:"$totalGeralAno", $notasGeral:"$numberNoteAno", 
             $manutencaoV:"$manutencao", $combustivelV:"$combustivel", $passagensV:"$passagens", 
             $assinaturaV:"$assinatura", $divulgacaoV:"$divulgacao", $telefoniaV:"$telefonia", 
             $postaisV:"$postais", $hospedagemV:"$hospedagem", $taxiV:"$taxi", $locacaoV:"$locacao",
             $consultoriaV:"$consultoria", $segurancaV:"$seguranca", $cursoV:"$curso", 
             $alimentacaoV:"$alimentacao", $outrosV:"$outros"}}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(listTotal.toByteArray())
            fos.flush()
            fos.close()
            println("Gravou gasto do ano $anoSoma")
        } catch (e: java.lang.Exception) { }

        // Gera JSON ranking do ano
        try {
            val ranking = """{"ranking": $listNomePrint}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/ranking$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(ranking.toByteArray())
            fos.flush()
            fos.close()
            println("Gravou ranking do ano $anoSoma")
        } catch (e: java.lang.Exception) {
        }

        when (anoSoma) {
            2023 -> {
                // Gera JSON Gastos de todos os anos
                val listSomaTotal =
                    """{"gastoGeral": {$totalGeralV:"$totalGeralSoma", $notasGeral:"$numberNoteTotal", 
                    $manutencaoV:"$manutencaoT", $combustivelV:"$combustivelT", $passagensV:"$passagensT",
                    $assinaturaV:"$assinaturaT", $divulgacaoV:"$divulgacaoT", $telefoniaV:"$telefoniaT", 
                    $postaisV:"$postaisT", $hospedagemV:"$hospedagemT", $taxiV:"$taxiT", $locacaoV:"$locacaoT",
                    $consultoriaV:"$consultoriaT", $segurancaV:"$segurancaT", $cursoV:"$cursoT", 
                    $alimentacaoV:"$alimentacaoT", $outrosV:"$outrosT"}"""

                try {
                    val geral = """{"total": $listSomaTotal}"""
                    val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/geral")
                    val fos = FileOutputStream(arq)
                    fos.write(geral.toByteArray())
                    fos.flush()
                    fos.close()
                    println("Gravou gasto geral")
                } catch (e: java.lang.Exception) {
                }

                // Gera JSON ranking do ano
                listNomeGastoRankingGeral.forEach {
                    listNomePrintGeral
                        .add("""{"id":"${it.id}", "nome":"${it.nome}", "foto":"${it.foto}", "gasto":"${it.gasto}", "partido":"${it.partido}", "estado":"${it.estado}"}""".trimMargin())
                }

                try {
                    val ranking = """{"rankingGeral": $listNomePrintGeral}"""
                    val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/rankingGeral")
                    val fos = FileOutputStream(arq)
                    fos.write(ranking.toByteArray())
                    fos.flush()
                    fos.close()
                    println("Gravou ranking geral")
                } catch (e: java.lang.Exception) {
                }
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

                // inicia nova busca por gastos no ano seguinte 2011 -> 2012 -> ...
                anoSoma += 1
                countDeputado = 0
                totalGeralAno = 0
                year += 1
                listNomeGastoAnoRanking = arrayListOf()
                listNomePrint = arrayListOf()
                println("Zerou contadores - Iniciando Ano $anoSoma")
                getInfoDeputado()
            }
        }
    }
}