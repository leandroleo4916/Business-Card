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
import br.com.dio.businesscard.ui.remote.ApiServiceLegislatura
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
    private var countRanking = 0
    private var anoSoma = 2011
    private var tentativa = 0
    private var listGastoGeral: ListSenado = arrayOf()
    private var listDeputados: ArrayList<Parlamentar> = arrayListOf()
    private var listNoteApi: ArrayList<String> = arrayListOf()

    private var listRankingGeral: ArrayList<SenadorRanking> = arrayListOf()
    private var listRankingAno: ArrayList<String> = arrayListOf()
    private var totalParlamentarAno = 0

    private var numberNoteAno = 0
    private var totalAno = 0
    private var aluguelA = 0
    private var divulgacaoA = 0
    private var passagensA = 0
    private var contratacaoA = 0
    private var locomocaoA = 0
    private var aquisicaoA = 0
    private var outrosA = 0

    private var numberNoteAnoV = """"notas""""
    private var totalAnoV = """"total""""
    private var aluguelV = """"aluguel""""
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

    //----------------------------------------------------------------------------------------//

    private fun observer() {

        val anoLegislatura =
            when(anoSoma){
                2011 -> 54
                2012 -> 54
                2013 -> 54
                2014 -> 54
                2015 -> 55
                2016 -> 55
                2017 -> 55
                2018 -> 55
                2019 -> 56
                2020 -> 56
                2021 -> 56
                2022 -> 56
                else -> 57
            }
        val retrofit = Retrofit.createService(ApiServiceLegislatura::class.java)
        val call: Call<SenadorLegislatura> = retrofit.getLegislatura(anoLegislatura.toString())

        call.enqueue(object : Callback<SenadorLegislatura> {
            override fun onResponse(call: Call<SenadorLegislatura>, despesas: Response<SenadorLegislatura>) {
                when (despesas.code()) {
                    200 -> {
                        println("Baixou lista de senadores")
                        if (despesas.body() != null) {
                            listDeputados = despesas.body()!!.listaParlamentarLegislatura
                                .parlamentares.parlamentar as ArrayList<Parlamentar>
                        }
                        observerGasto()
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

            override fun onFailure(call: Call<SenadorLegislatura>, t: Throwable) {
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

    private fun observerGasto() {

        val retrofit = Retrofit.createService(ApiServiceSenado::class.java)
        val call: Call<ListSenado> = retrofit.getDataSenado(anoSoma.toString())

        call.enqueue(object : Callback<ListSenado> {
            override fun onResponse(call: Call<ListSenado>, despesas: Response<ListSenado>) {
                when (despesas.code()) {
                    200 -> {
                        println("Baixou lista de gasto")
                        println("--------------------------------------------")
                        if (despesas.body() != null) {
                            listGastoGeral = despesas.body()!!
                            numberNoteAno += despesas.body()!!.size
                            numberNoteTotal += despesas.body()!!.size
                            orderList()
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

    private fun orderList(){
        println("Iniciou ordenação da lista")
        listDeputados.forEach {

            println("Pegando notas de ${it.identificacaoParlamentar.nomeParlamentar}")
            val n1 = deleteAccent(it.identificacaoParlamentar.nomeParlamentar)
            binding.textTotalProcessoSenador.text = "$sizeTotalProcess Deputados"
            binding.textNomeEGastoSenador.text = "Contando notas de $n1"
            sizeTotalProcess += 1

            for (item in listGastoGeral){
                val n2 = deleteAccent(item.nomeSenador)
                if (n1 == n2){
                    // Gasto por ano
                    addNoteParlamentarToListAno(item)
                    // Calcula gasto
                    calcValueNotes(item.tipoDespesa, item.valorReembolsado.toInt())
                }
            }
            if (totalParlamentarAno != 0) {
                binding.textTotalGeralSenador.text = "$n1 - $totalParlamentarAno"
                addParlamentarToListRankingAno(it)
                addParlamentarToListRankingGeral(it)
                recNoteParlamentar(n1)
            }
        }
        recAno()
        recRankingAno()
        recGeral()
    }

    private fun addNoteParlamentarToListAno(it: SenadoDataClass) {
        listNoteApi.add("""{"ano":"${it.ano}", "mes":"${it.mes}", "senador":"${it.nomeSenador}", 
                            "tipoDespesa":"${it.tipoDespesa}", "cnpjCpf":"${it.cpfCnpj}", "fornecedor":"${it.fornecedor}", 
                            "documento":"${it.documento}", "data":"${it.data}", "detalhamento":"${it.detalhamento}", 
                            "valorReembolsado":"${it.valorReembolsado}", "codDocumento":"${it.id}"}""".trimMargin())
    }

    private fun calcValueNotes(note: String, valor: Int) {

        when (note) {
            "Aluguel de imóveis para escritório político, compreendendo despesas concernentes a eles." -> {
                aluguelA += valor
                aluguelG += valor
            }
            "Locomoção, hospedagem, alimentação, combustíveis e lubrificantes" -> {
                locomocaoA += valor
                locomocaoG += valor
            }
            "Divulgação da atividade parlamentar" -> {
                divulgacaoA += valor
                divulgacaoG += valor
            }
            "Contratação de consultorias, assessorias, pesquisas, trabalhos técnicos e outros serviços de apoio ao exercício do mandato parlamentar" -> {
                contratacaoA += valor
                contratacaoG += valor
            }
            "Passagens aéreas, aquáticas e terrestres nacionais" -> {
                passagensA += valor
                passagensG += valor
            }
            "Aquisição de material de consumo para uso no escritório político, inclusive aquisição ou locação de software, despesas postais, aquisição de publicações, locação de móveis e de equipamentos. " -> {
                aquisicaoA += valor
                aquisicaoG += valor
            }
            else -> {
                outrosA += valor
                outrosG += valor
            }
        }
        totalGeralG
        totalParlamentarAno += valor
        totalAno += valor
    }

    private fun addParlamentarToListRankingAno(parlamentar: Parlamentar) {
        parlamentar.identificacaoParlamentar.run {
            listRankingAno.add(
                """{"id":"${this.codigoParlamentar}", "nome":"${this.nomeParlamentar}", 
                    |"foto":"${this.urlFotoParlamentar ?: ""}", "gasto":"$totalParlamentarAno", 
                    |"partido":"${this.siglaPartidoParlamentar}", "estado":"${this.ufParlamentar}"}""".trimMargin()
            )
            binding.textRanking.text = "$countRanking adicionado no ranking"
            countRanking += 1
        }
    }

    private fun addParlamentarToListRankingGeral(parlamentar: Parlamentar) {
        parlamentar.identificacaoParlamentar.run {

            if (listGastoGeral.isNotEmpty()) {
                val n1 = deleteAccent(this.nomeParlamentar)
                var contain = false

                listRankingGeral.forEach {
                    val n2 = deleteAccent(it.nome)
                    if (n1 == n2) {
                        it.gasto += totalParlamentarAno
                        contain = true
                    }
                }
                if (!contain) {
                    listRankingGeral.add(
                        SenadorRanking(
                            this.codigoParlamentar,
                            this.nomeParlamentar,
                            this.urlFotoParlamentar ?: "",
                            totalParlamentarAno,
                            this.siglaPartidoParlamentar ?: "",
                            parlamentar.mandatos.mandato.ufParlamentar
                        )
                    )
                } else {
                    contain = false
                }
            } else {
                listRankingGeral.add(
                    SenadorRanking(
                        this.codigoParlamentar,
                        this.nomeParlamentar,
                        this.urlFotoParlamentar ?: "",
                        totalParlamentarAno,
                        this.siglaPartidoParlamentar ?: "",
                        parlamentar.mandatos.mandato.ufParlamentar
                    )
                )
            }
        }
    }

    private fun clearValuesGastosAno(){
        listRankingAno = arrayListOf()
        numberNoteAno = 0
        totalAno = 0
        aluguelA = 0
        divulgacaoA = 0
        passagensA = 0
        contratacaoA = 0
        locomocaoA = 0
        aquisicaoA = 0
        outrosA = 0
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

    // Grava notas de cada Parlamentar
    private fun recNoteParlamentar(nome: String){
        println("Iniciou gravação notas $nome")
        try {
            val total = """{"gastosSenador": $listNoteApi}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenado/$anoSoma/$nome")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            totalParlamentarAno = 0
            listNoteApi = arrayListOf()
            println("Gravou gasto $nome - $anoSoma")
            println("--------------------------------------------")
        } catch (e: java.lang.Exception) { }
    }

    private fun recAno() {
        println("Iniciou gravação de gastos $anoSoma")
        try {
            val listAno = """{$totalAnoV: "$totalAno", $numberNoteAnoV: "$numberNoteAno", 
            |$aluguelV: "$aluguelA", $divulgacaoV: "$divulgacaoA", $contratacaoV: "$contratacaoA", 
            |$locomocaoV: "$locomocaoA", $passagensV: "$passagensA", $aquisicaoV: "$aquisicaoA", 
            |$outrosV: "$outrosA"}""".trimMargin()

            val total = """{"gastoGeral": $listAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenadoAno/$anoSoma/$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            println("Gravou gasto $anoSoma")
        } catch (e: java.lang.Exception) { }
    }

    private fun recRankingAno(){
        try {
            val total = """{"rankingSenado": $listRankingAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/rankingSenado/$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            println("Gravou ranking $anoSoma")
        } catch (e: java.lang.Exception) { }
        listRankingAno = arrayListOf()
        clearValuesGastosAno()
        observer()
    }

    private fun recGeral(){
        if (anoSoma == 2023){
            try {
                val listAno = """{$totalAnoV: "$totalGeralG", $numberNoteAnoV: "$numberNoteTotal", 
                |$aluguelV: "$aluguelG", $divulgacaoV: "$divulgacaoG", $contratacaoV: "$contratacaoG", 
                |$locomocaoV: "$locomocaoG", $passagensV: "$passagensG", $aquisicaoV: "$aquisicaoG", 
                |$outrosV: "$outrosG"}""".trimMargin()

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
                    listRankingAll.add("""{"id":"${it.id}", "nome":"${it.nome}", 
                    |"foto":"${it.foto}", "gasto":"${it.gasto}", "partido":"${it.partido}", 
                    |"estado":"${it.estado}"}""".trimMargin())
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
            binding.textAno.text = "$anoSoma"
            println("Ano $anoSoma")
        }
    }
}