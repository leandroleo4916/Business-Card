package br.com.dio.businesscard.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.dio.businesscard.databinding.ActivitySenadoBinding
import br.com.dio.businesscard.ui.dataclass.*
import br.com.dio.businesscard.ui.remote.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.Normalizer
import java.util.regex.Pattern

class SenadoActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySenadoBinding.inflate(layoutInflater) }
    private var sizeTotalProcess = 0
    private var anoSoma = 2011
    private var tentativa = 0
    private var listGastoGeral: ListSenado = arrayOf()
    private var listGastoDimension: ArrayList<String> = arrayListOf()
    private var listRankingGeral: ArrayList<SenadorRanking> = arrayListOf()
    private var rankingAno: ArrayList<SenadorRanking> = arrayListOf()
    private var contain = false
    private var containAno = false
    private var nome = ""

    private var numberNoteAno = 0
    private var totalAno = 0.0
    private var aluguel = 0.0
    private var divulgacao = 0.0
    private var passagens = 0.0
    private var contratacao = 0.0
    private var locomocao = 0.0
    private var aquisicao = 0.0
    private var outros = 0.0

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
    private var totalGeralG = 0.0
    private var aluguelG = 0.0
    private var divulgacaoG = 0.0
    private var passagensG = 0.0
    private var contratacaoG = 0.0
    private var locomocaoG = 0.0
    private var aquisicaoG = 0.0
    private var outrosG = 0.0

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
                        val despesa = despesas.body()
                        if (despesa != null) {
                            listGastoGeral = despesa
                            val size = despesa.size
                            numberNoteAno += size
                            numberNoteTotal += size
                            processListGasto()
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

        listGastoGeral.forEach {
            addRankingAno(it.nomeSenador, it.valorReembolsado)

            if (nome != ""){
                if (nome == it.nomeSenador){
                    calcValueNotes(it.tipoDespesa, it.valorReembolsado)
                }
                else {
                    totalAno += aluguel+locomocao+divulgacao+contratacao+passagens+aquisicao+outros
                    totalGeralG += totalAno
                    addRankingGeral(nome, totalAno)
                    nome = it.nomeSenador
                    aluguel = 0.0
                    locomocao = 0.0
                    divulgacao = 0.0
                    contratacao = 0.0
                    passagens = 0.0
                    aquisicao = 0.0
                    outros = 0.0
                }
            }
            else {
                nome = it.nomeSenador
                calcValueNotes(it.tipoDespesa, it.valorReembolsado)
            }
        }

        totalAno = 0.0
        nome = ""
        recAno()
    }

    private fun addRankingGeral(nome: String, valor: Double){
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

    private fun addRankingAno(nome: String, valor: Double){
        for (i in rankingAno){
            if (i.nome == nome){
                i.gasto += valor
                containAno = true
                break
            }
        }
        containAno = if (!containAno){
            rankingAno.add(SenadorRanking(nome, valor))
            false
        } else false
    }

    private fun calcValueNotes(note: String, valor: Double) {

        when (note) {
            "Aluguel de imóveis para escritório político, compreendendo despesas concernentes a eles." -> {
                aluguel += valor
                aluguelG += valor
            }
            "Locomoção, hospedagem, alimentação, combustíveis e lubrificantes" -> {
                locomocao += valor
                locomocaoG += valor
            }
            "Divulgação da atividade parlamentar" -> {
                divulgacao += valor
                divulgacaoG += valor
            }
            "Contratação de consultorias, assessorias, pesquisas, trabalhos técnicos e outros serviços de apoio ao exercício do mandato parlamentar" -> {
                contratacao += valor
                contratacaoG += valor
            }
            "Passagens aéreas, aquáticas e terrestres nacionais" -> {
                passagens += valor
                passagensG += valor
            }
            "Aquisição de material de consumo para uso no escritório político, inclusive aquisição ou locação de software, despesas postais, aquisição de publicações, locação de móveis e de equipamentos. " -> {
                aquisicao += valor
                aquisicaoG += valor
            }
            else -> {
                outros += valor
                outrosG += valor
            }
        }
    }

    private fun recAno() {
        try {
            val listAno = """$totalAnoV: "$totalAno", $numberNoteAnoV: "$numberNoteAno", 
            |$aluguelV: "$aluguel", $divulgacaoV: "$divulgacao", $contratacaoV: "$contratacao", 
            |$locomocaoV: "$locomocao", $passagensV: "$passagens", $aquisicaoV: "$aquisicao", 
            |$outrosV: "$outros"""".trimMargin()

            val total = """{"total": $listAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenado/geral")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) { }

        try {
            val listRankingAno: ArrayList<String> = arrayListOf()
            rankingAno.forEach {
                val gasto = it.gasto.toInt()
                listRankingAno.add(""""nome":"${it.nome}", "gasto":"$gasto"""")
            }
            val total = """{"ranking": $listRankingAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/rankingSenado/geral")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) { }

        if (anoSoma == 2023){
            try {
                val listAno = """$totalAnoV: "$totalGeralG", $numberNoteAnoV: "$numberNoteTotal", 
                |$aluguelV: "$aluguelG", $divulgacaoV: "$divulgacaoG", $contratacaoV: "$contratacaoG", 
                |$locomocaoV: "$locomocaoG", $passagensV: "$passagensG", $aquisicaoV: "$aquisicaoG", 
                |$outrosV: "$outrosG"""".trimMargin()

                val total = """{"total": $listAno}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeralSenado/geral")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: java.lang.Exception) { }

            try {
                val listRankingAll: ArrayList<String> = arrayListOf()
                listRankingGeral.forEach {
                    val gasto = it.gasto.toInt()
                    listRankingAll.add(""""nome":"${it.nome}", "gasto":"$gasto"""")
                }
                val total = """{"ranking": $listRankingAll}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/rankingGeralSenado/geral")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: java.lang.Exception) { }
        }
        anoSoma += 1
        observer()
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