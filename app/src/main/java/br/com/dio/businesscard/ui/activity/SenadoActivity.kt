package br.com.dio.businesscard.ui.activity

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
import br.com.dio.businesscard.ui.utils.DeleteAccent
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class SenadoActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySenadoBinding.inflate(layoutInflater) }
    private val deleteAccent: DeleteAccent by inject()
    private var countRanking = 0
    private var anoSoma = 2011
    private var tentativa = 0
    private var listGastoGeral: ListSenado = arrayOf()
    private var listSenadores: ArrayList<Parlamentar> = arrayListOf()
    private var listSenadoresAdded: ArrayList<Parlamentar> = arrayListOf()
    private var anoLegislatura = 54
    private var listNoteApi: ArrayList<String> = arrayListOf()

    private var listRankingGeral: ArrayList<SenadorRanking> = arrayListOf()
    private var listRankingListAno: ArrayList<SenadorRanking> = arrayListOf()
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
    private var segurancaA = 0
    private var outrosA = 0

    private var numberNoteAnoV = """"notas""""
    private var totalAnoV = """"total""""
    private var aluguelV = """"aluguel""""
    private var divulgacaoV = """"divulgacao""""
    private var passagensV = """"passagens""""
    private var contratacaoV = """"contratacao""""
    private var locomocaoV = """"locomocao""""
    private var aquisicaoV = """"aquisicao""""
    private var segurancaV = """"segurança""""
    private var outrosV = """"outros""""

    private var numberNoteTotal = 0
    private var totalGeralG = 0
    private var aluguelG = 0
    private var divulgacaoG = 0
    private var passagensG = 0
    private var contratacaoG = 0
    private var locomocaoG = 0
    private var aquisicaoG = 0
    private var segurancaG = 0
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

    // Busca lista de senadores por legislatura
    private fun observer() {

        val retrofit = Retrofit.createService(ApiServiceLegislatura::class.java)
        val call: Call<SenadorLegislatura> = retrofit.getLegislatura(anoLegislatura.toString())

        call.enqueue(object : Callback<SenadorLegislatura> {
            override fun onResponse(call: Call<SenadorLegislatura>, despesas: Response<SenadorLegislatura>) {
                when (despesas.code()) {
                    200 -> {
                        println("Baixou lista de senadores")
                        binding.textProcessoSen.text = "Baixou lista de senadores"
                        if (despesas.body() != null) {
                            listSenadores = despesas.body()!!.listaParlamentarLegislatura
                                .parlamentares.parlamentar as ArrayList<Parlamentar>
                        }
                        uneListaSenadores()
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

    // Une lista de senadores da lesgislatura 54 a 57
    private fun uneListaSenadores(){

        println("Unindo lista de senadores...")
        binding.textProcessoSen.text = "Baixou lista de senadores"
        listSenadoresAdded =
            if (listSenadoresAdded.isEmpty()) listSenadores
            else (listSenadores + listSenadoresAdded).distinct() as ArrayList<Parlamentar>

        anoLegislatura++
        if (anoLegislatura <= 57) observer()
        else observerGasto()
    }

    // Busca gasto do ano 2011 a 2023
    private fun observerGasto() {

        println("Baixando lista de gastos ano: $anoSoma ...")
        binding.textProcessoSen.text = "Baixando lista de gastos ano: $anoSoma ..."
        val retrofit = Retrofit.createService(ApiServiceSenado::class.java)
        val call: Call<ListSenado> = retrofit.getDataSenado(anoSoma.toString())

        call.enqueue(object : Callback<ListSenado> {
            override fun onResponse(call: Call<ListSenado>, despesas: Response<ListSenado>) {
                when (despesas.code()) {
                    200 -> {
                        println("Baixou lista de gasto ano: $anoSoma")
                        println("--------------------------------------------")
                        binding.textProcessoSen.text = "Baixou lista de gasto ano: $anoSoma"
                        if (despesas.body() != null) {
                            listGastoGeral = despesas.body()!!
                            numberNoteAno += despesas.body()!!.size
                            numberNoteTotal += despesas.body()!!.size
                            addAllNotesPerSenador()
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

    // Adiciona note para cada senador
    private fun addAllNotesPerSenador(){

        val listRemove: MutableList<SenadoDataClass> = arrayListOf()
        var listIndex: MutableList<SenadoDataClass> = arrayListOf()

        listGastoGeral.forEach {
            calcValueNotes(it.tipoDespesa, it.valorReembolsado.toInt())
            listRemove.add(it)
        }

        for (senador in listSenadoresAdded){
            val nome = senador.identificacaoParlamentar.nomeParlamentar.lowercase()
            binding.textNomeEGastoSenador.text = nome
            var valorNota = 0
            for (note in listRemove){
                if (nome == note.nomeSenador.lowercase()){
                    valorNota += note.valorReembolsado.toInt()
                    addNoteParlamentarToListAno(note)
                    listIndex.add(note)
                }
                addSenadorRankingAno(senador, note.valorReembolsado.toInt())
                addParlamentarToListRankingGeral(senador, note.valorReembolsado.toInt())
            }

            listRemove.removeAll(listIndex)

            listIndex = arrayListOf()
            if (listNoteApi.isNotEmpty()) {
                val nomeFormat = deleteAccent.deleteAccent(nome)
                recNoteParlamentar(nomeFormat)
            }
        }
        recAno()
        recRankingAno()
        recGeral()
    }

    //Calcula ranking por ano
    private fun addSenadorRankingAno(parlamentar: Parlamentar, note: Int) {

        parlamentar.identificacaoParlamentar.run {
            if (listRankingListAno.isNotEmpty()) {
                val nome1 = deleteAccent.deleteAccent(this.nomeParlamentar)
                var contain = false

                listRankingListAno.forEach {
                    val nome2 = deleteAccent.deleteAccent(it.nome)
                    if (nome1 == nome2) {
                        it.gasto += note
                        contain = true
                    }
                }
                if (!contain) addSenadorListRankingAno(parlamentar, note)
                else contain = false

            }
            else addSenadorListRankingAno(parlamentar, note)
        }
    }

    private fun addSenadorListRankingAno(parlamentar: Parlamentar, note: Int) {
        parlamentar.identificacaoParlamentar.run {
            listRankingListAno.add(
                SenadorRanking(
                    codigoParlamentar,
                    nomeParlamentar,
                    urlFotoParlamentar ?: "",
                    note,
                    siglaPartidoParlamentar ?: "",
                    parlamentar.mandatos.mandato.ufParlamentar
                )
            )
        }
        binding.textRanking.text = "$countRanking adicionado no ranking"
        countRanking += 1
    }

    // Adiciona nota de cada senador
    private fun addNoteParlamentarToListAno(it: SenadoDataClass) {
        listNoteApi.add("""{"ano":"${it.ano}", "mes":"${it.mes}", "senador":"${it.nomeSenador}", 
                            "tipoDespesa":"${it.tipoDespesa}", "cnpjCpf":"${it.cpfCnpj}", "fornecedor":"${it.fornecedor}", 
                            "documento":"${it.documento}", "data":"${it.data}", "detalhamento":"${it.detalhamento}", 
                            "valorReembolsado":"${it.valorReembolsado}", "codDocumento":"${it.id}"}""".trimMargin())
    }

    // Calcula notas por tipo de gasto
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
            "Serviços de Segurança Privada" -> {
                segurancaA += valor
                segurancaG += valor
            }
            else -> {
                outrosA += valor
                outrosG += valor
            }
        }
        totalParlamentarAno += valor
        totalAno += valor
    }

    //Calcula ranking geral
    private fun addParlamentarToListRankingGeral(parlamentar: Parlamentar, note: Int) {
        parlamentar.identificacaoParlamentar.run {

            if (listGastoGeral.isNotEmpty()) {
                val n1 = deleteAccent.deleteAccent(this.nomeParlamentar)
                var contain = false

                listRankingGeral.forEach {
                    val n2 = deleteAccent.deleteAccent(it.nome)
                    if (n1 == n2) {
                        it.gasto += note
                        contain = true
                    }
                }
                if (!contain) addSenadorListRankingGeral(parlamentar, note)
                else contain = false
            }
            else addSenadorListRankingGeral(parlamentar, note)
        }
    }

    private fun addSenadorListRankingGeral(parlamentar: Parlamentar, note: Int) {

        parlamentar.identificacaoParlamentar.run {
            listRankingGeral.add(
                SenadorRanking(
                    this.codigoParlamentar,
                    this.nomeParlamentar,
                    this.urlFotoParlamentar ?: "",
                    note,
                    this.siglaPartidoParlamentar ?: "",
                    parlamentar.mandatos.mandato.ufParlamentar
                )
            )
        }
    }

    // Limpa valores ao terminar contagem das nota por ano
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

    // Grava notas de cada Parlamentar
    private fun recNoteParlamentar(nome: String){
        println("Iniciou gravação notas $nome ...")
        binding.textProcessoSen.text = "Iniciou gravação notas $nome ..."
        try {
            val total = """{"gastosSenador": $listNoteApi}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoPorSenador/$anoSoma/$nome")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            totalParlamentarAno = 0
            listNoteApi = arrayListOf()
            println("Gravou gasto $nome - $anoSoma")
            println("--------------------------------------------")
            binding.textProcessoSen.text = "Gravou gasto $nome - $anoSoma"
        } catch (e: java.lang.Exception) { }
    }

    private fun recAno() {
        println("Iniciou gravação de gastos $anoSoma ...")
        binding.textProcessoSen.text = "Iniciou gravação de gastos $anoSoma ..."
        try {
            val listAno = """{$totalAnoV: "$totalAno", $numberNoteAnoV: "$numberNoteAno", 
            |$aluguelV: "$aluguelA", $divulgacaoV: "$divulgacaoA", $contratacaoV: "$contratacaoA", 
            |$locomocaoV: "$locomocaoA", $passagensV: "$passagensA", $aquisicaoV: "$aquisicaoA", 
            |$segurancaV: "$segurancaA", $outrosV: "$outrosA"}""".trimMargin()

            val total = """{"gastoGeral": $listAno}"""
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenadoAno/$anoSoma")
            val fos = FileOutputStream(arq)
            fos.write(total.toByteArray())
            fos.flush()
            fos.close()

            println("Gravou gasto $anoSoma")
            binding.textProcessoSen.text = "Gravou gasto $anoSoma"
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
            binding.textProcessoSen.text = "Gravou ranking $anoSoma"
        } catch (e: java.lang.Exception) { }
        listRankingAno = arrayListOf()
        clearValuesGastosAno()
        println("Limpou valores Gasto $anoSoma")
        println("-----------------------------")
        binding.textProcessoSen.text = "Limpou valores Gasto $anoSoma"
    }

    private fun recGeral(){
        if (anoSoma == 2023){
            try {
                val listAno = """{$totalAnoV: "$totalGeralG", $numberNoteAnoV: "$numberNoteTotal", 
                |$aluguelV: "$aluguelG", $divulgacaoV: "$divulgacaoG", $contratacaoV: "$contratacaoG", 
                |$locomocaoV: "$locomocaoG", $passagensV: "$passagensG", $aquisicaoV: "$aquisicaoG", 
                |$segurancaV: "$segurancaG", $outrosV: "$outrosG"}""".trimMargin()

                val total = """{"totalGeral": $listAno}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/gastoSenadoGeral")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()

                println("Gravou gasto geral")
                binding.textProcessoSen.text = "Gravou gasto geral"
            } catch (e: java.lang.Exception) { }

            try {
                val listRankingAll: ArrayList<String> = arrayListOf()
                listRankingGeral.forEach {
                    listRankingAll.add("""{"id":"${it.id}", "nome":"${it.nome}", 
                    |"foto":"${it.foto}", "gasto":"${it.gasto}", "partido":"${it.partido}", 
                    |"estado":"${it.estado}"}""".trimMargin())
                }
                val total = """{"rankingGeral": $listRankingAll}"""
                val arq = File(Environment.getExternalStorageDirectory(), "/rankingSenadoGeral")
                val fos = FileOutputStream(arq)
                fos.write(total.toByteArray())
                fos.flush()
                fos.close()

                println("Gravou ranking geral")
                binding.textProcessoSen.text = "Gravou ranking geral"
            } catch (e: java.lang.Exception) { }
        }
        else {
            anoSoma += 1
            binding.textAno.text = "$anoSoma"
            observerGasto()
            println("Ano $anoSoma")
            println("---------------------------")
        }
    }
}