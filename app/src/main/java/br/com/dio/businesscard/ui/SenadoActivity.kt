package br.com.dio.businesscard.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.dio.businesscard.databinding.ActivitySenadoBinding
import br.com.dio.businesscard.ui.dataclass.ListSenado
import br.com.dio.businesscard.ui.dataclass.SenadoDataClass
import br.com.dio.businesscard.ui.dataclass.SenadorRanking
import br.com.dio.businesscard.ui.remote.ApiServiceSenado
import br.com.dio.businesscard.ui.remote.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer
import java.util.regex.Pattern

class SenadoActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySenadoBinding.inflate(layoutInflater) }
    private var sizeTotalProcess = 0
    private var anoSoma = 2011
    private var tentativa = 0
    private var listGastoGeral: ListSenado = arrayOf()
    private var listNoteApi: ArrayList<String> = arrayListOf()
    private var listOrder: MutableList<SenadoDataClass> = arrayListOf()
    private var listRankingGeral: ArrayList<SenadorRanking> = arrayListOf()
    private var listRankingAno: ArrayList<String> = arrayListOf()
    private var contain = false
    private var nome = ""

    private var numberNoteAno = 0
    private var totalAno = 0
    private var aluguel = 0
    private var divulgacao = 0
    private var passagens = 0
    private var contratacao = 0
    private var locomocao = 0
    private var aquisicao = 0
    private var outros = 0

    private var totalParlamentarAno = 0
    private var aluguelP = 0
    private var divulgacaoP = 0
    private var passagensP = 0
    private var contratacaoP = 0
    private var locomocaoP = 0
    private var aquisicaoP = 0
    private var outrosP = 0

    private var numberNoteAnoV = """"notas""""
    private var totalAnoV = """"total""""
    private var aluguelV = """"alguel""""
    private var divulgacaoV = """"divulgacao""""
    private var passagensV = """"passagens""""
    private var contratacaoV = """"contratacao""""
    private var locomocaoV = """"locomocao""""
    private var aquisicaoV = """"aquisicao""""
    private var outrosV = """"outros""""

    private var numberNoteTotal = 0
    private var totalGeralG = 0
    private var aluguelG = 0
    private var divulgacaoG = 0
    private var passagensG = 0
    private var contratacaoG = 0
    private var locomocaoG = 0
    private var aquisicaoG = 0
    private var outrosG = 0

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
            textProcessoSen.setOnClickListener {
                binding.textProcessoSen.text = "Processando dados..."
                println("Iniciou processo")
                observer()
            }
        }
    }

    private fun observer() {

        val retrofit = Retrofit.createService(ApiServiceSenado::class.java)
        val call: Call<ListSenado> = retrofit.getDataSenado(anoSoma.toString())

        call.enqueue(object : Callback<ListSenado> {
            override fun onResponse(call: Call<ListSenado>, despesas: Response<ListSenado>) {
                when (despesas.code()) {
                    200 -> {
                        println("Baixou lista de gasto")
                        if (despesas.body() != null) {
                            listGastoGeral = despesas.body()!!
                            numberNoteAno += despesas.body()!!.size
                            numberNoteTotal += despesas.body()!!.size

                            //processListGasto()
                        }
                    }
                    429 -> observer()
                    else -> {
                        if (tentativa != 3) {
                            tentativa =+ 1
                            observer()
                        }
                        println(despesas.message())
                        binding.textErro.text = "Erro em API - Tentando novamente $tentativa"
                        Toast.makeText(application, despesas.message(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ListSenado>, t: Throwable) {
                if (tentativa != 3) {
                    tentativa =+ 1
                    observer()
                }
                println(t.message)
                binding.textErro.text = "Erro em API - Tentando novamente $tentativa"
                Toast.makeText(application, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun processListGasto(){

        println("Iniciou contagem das notas")
        listOrder.forEach {

            totalAno += it.valorReembolsado.toInt()
            totalGeralG += it.valorReembolsado.toInt()

            if (nome != ""){
                if (nome != it.nomeSenador){
                    println("$nome - RS $totalParlamentarAno")
                    listRankingAno.add(""""nome":"$nome", "gasto":"$totalParlamentarAno"""")
                    addRankingGeral(nome, totalParlamentarAno)
                    aluguelP = 0
                    divulgacaoP = 0
                    passagensP = 0
                    contratacaoP = 0
                    locomocaoP = 0
                    aquisicaoP = 0
                    outrosP = 0
                    totalParlamentarAno = 0
                    nome = it.nomeSenador
                    calcValueNotes(it.tipoDespesa, it.valorReembolsado.toInt())
                }
                else calcValueNotes(it.tipoDespesa, it.valorReembolsado.toInt())
            }
            else {
                calcValueNotes(it.tipoDespesa, it.valorReembolsado.toInt())
                nome = it.nomeSenador
            }
            sizeTotalProcess += 1
        }
        nome = ""
        recAno()
    }

    private fun addRankingGeral(nome: String, valor: Int){
        for (i in listRankingGeral) {
            if (i.nome.contains(nome)){
                i.gasto += valor
                contain = true
                break
            }
        }
        contain = if (!contain) {
            listRankingGeral.add(SenadorRanking(nome, valor))
            false
        } else false
    }

    private fun calcValueNotes(note: String, valor: Int) {

        binding.textTotalProcessoSenador.text = "$sizeTotalProcess notas processadas"
        when (note) {
            "Aluguel de imóveis para escritório político, compreendendo despesas concernentes a eles." -> {
                aluguel += valor
                aluguelP += valor
                aluguelG += valor
            }
            "Locomoção, hospedagem, alimentação, combustíveis e lubrificantes" -> {
                locomocao += valor
                locomocaoP += valor
                locomocaoG += valor
            }
            "Divulgação da atividade parlamentar" -> {
                divulgacao += valor
                divulgacaoP += valor
                divulgacaoG += valor
            }
            "Contratação de consultorias, assessorias, pesquisas, trabalhos técnicos e outros serviços de apoio ao exercício do mandato parlamentar" -> {
                contratacao += valor
                contratacaoP += valor
                contratacaoG += valor
            }
            "Passagens aéreas, aquáticas e terrestres nacionais" -> {
                passagens += valor
                passagensP += valor
                passagensG += valor
            }
            "Aquisição de material de consumo para uso no escritório político, inclusive aquisição ou locação de software, despesas postais, aquisição de publicações, locação de móveis e de equipamentos. " -> {
                aquisicao += valor
                aquisicaoP += valor
                aquisicaoG += valor
            }
            else -> {
                outros += valor
                outrosP += valor
                outrosG += valor
            }
        }
        totalParlamentarAno += valor
    }

    private fun recAno() {
        println("Iniciou gravação de $anoSoma no aparelho")
        try {
            val listAno = """$totalAnoV: "$totalAno", $numberNoteAnoV: "$numberNoteAno", 
            |$aluguelV: "$aluguel", $divulgacaoV: "$divulgacao", $contratacaoV: "$contratacao", 
            |$locomocaoV: "$locomocao", $passagensV: "$passagens", $aquisicaoV: "$aquisicao", 
            |$outrosV: "$outros"""".trimMargin()

            val total = """{"total$anoSoma": $listAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenado/geral$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            println("Gravou gasto $anoSoma")
        } catch (e: java.lang.Exception) { }

        try {
            val total = """{"ranking$anoSoma": $listRankingAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/rankingSenado/geral$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            println("Gravou ranking $anoSoma")
        } catch (e: java.lang.Exception) { }

        if (anoSoma == 2023){
            try {
                val listAno = """$totalAnoV: "$totalGeralG", $numberNoteAnoV: "$numberNoteTotal", 
                |$aluguelV: "$aluguelG", $divulgacaoV: "$divulgacaoG", $contratacaoV: "$contratacaoG", 
                |$locomocaoV: "$locomocaoG", $passagensV: "$passagensG", $aquisicaoV: "$aquisicaoG", 
                |$outrosV: "$outrosG"""".trimMargin()

                val total = """{"totalGeral": $listAno}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenado/geralAll")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()

                println("Gravou gasto geral")
            } catch (e: java.lang.Exception) { }

            try {
                val listRankingAll: ArrayList<String> = arrayListOf()
                listRankingGeral.forEach {
                    val gasto = it.gasto
                    listRankingAll.add(""""nome":"${it.nome}", "gasto":"$gasto"""")
                }
                val total = """{"rankingGeral": $listRankingAll}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/rankingSenado/geralAll")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()

                println("Gravou ranking geral")
            } catch (e: java.lang.Exception) { }
        }
        else {
            anoSoma += 1
            totalAno = 0
            numberNoteAno = 0
            listRankingAno = arrayListOf()
            observer()
        }
    }

    //----------------------------------------------------------------------------------------//

    private fun orderList(){
        println("Iniciou ordenação da lista")
        var index = 0
        listGastoGeral.forEach {
            if (listOrder.isNotEmpty()){
                var igual = false
                for (i in listOrder){
                    if (it.nomeSenador == i.nomeSenador){
                        listOrder.add(index, it)
                        index = 0
                        igual = true
                        break
                    }
                    else {
                        index += 1
                    }
                }
                if (!igual){
                    listOrder.add(it)
                    index = 0
                    println("Processado ${it.nomeSenador}")
                }
            }
            else {
                listOrder.add(it)
                index += 1
            }
        }
        recNote()
    }

    private fun recNote(){

        if (anoSoma < 2015){

            listOrder.forEach {
                if (nome != ""){
                    if (nome == it.nomeSenador){
                        addNoteToList(it)
                    }
                    else {
                        try {
                            val nomeS = deleteAccent(nome)
                            val total = """{"$nomeS": $listNoteApi}"""
                            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenadoAno/$anoSoma/$nomeS")
                            val fos = FileOutputStream(arq)
                            fos.write(total.toByteArray())
                            fos.flush()
                            fos.close()
                            listNoteApi = arrayListOf()
                            println("Gravou gastos do $nome - $anoSoma")
                            nome = it.nomeSenador
                            addNoteToList(it)

                        } catch (e: java.lang.Exception) { println("Erro ao salvar") }
                    }
                }
                else {
                    nome = it.nomeSenador
                    addNoteToList(it)
                }
            }
            anoSoma += 1
            listNoteApi = arrayListOf()
            nome = ""
            observer()
        }
        else println("Fim do processo")
    }

    private fun addNoteToList(it: SenadoDataClass) {
        listNoteApi.add("""{"ano":"${it.ano}", "mes":"${it.mes}", "senador":"${it.nomeSenador}", 
                            "tipoDespesa":"${it.tipoDespesa}", "cnpjCpf":"${it.cpfCnpj}", "fornecedor":"${it.fornecedor}", 
                            "documento":"${it.documento}", "data":"${it.data}", "detalhamento":"${it.detalhamento}", 
                            "valorReembolsado":"${it.valorReembolsado}", "codDocumento":"${it.id}"}""".trimMargin())
    }

    private fun deleteAccent(str: String): String {
        var ret = ""
        val lower = str.lowercase()
        val nfdNormalizedString: String = Normalizer.normalize(lower, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val text = pattern.matcher(nfdNormalizedString).replaceAll("")
        val nome = text.split(" ")
        nome.forEach { ret += it }
        return ret
    }

}