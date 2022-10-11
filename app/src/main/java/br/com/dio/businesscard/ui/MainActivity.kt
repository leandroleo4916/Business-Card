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
    private var listGasto: ArrayList<DadoDespesas> = arrayListOf()
    private var listNomeGasto: ArrayList<NomeGastoTotal> = arrayListOf()
    private var gastoPorDeputado = 0
    private var nome = ""
    private var numberNote = 0
    private var position = 0
    private var sizeCount = 0
    private var page = 1
    private var year = 2022
    private var id = ""
    //-----------------------------------

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
                binding.textProcesso.text = "Processando..."
                observerListDeputado()
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
            download(list)

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
                            rec(listD.toString(), name)
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
                        rec(listD.toString(), name)
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
        nome.forEach{
            ret += it
        }
        return ret
    }

    // Busca lsita de deputados
    private fun observerListDeputado() {
        viewModelCamara.searchData(ordenarPor = "nome").observe(this) {
            it?.let { result ->
                when (result) {
                    is ResultRequest.Success -> {
                        result.dado?.let { deputados ->
                            listDeputado = deputados.dados as ArrayList
                            sizeCount = listDeputado.size
                            searchGasto()
                        }
                    }
                    is ResultRequest.Error -> {
                        result.exception.message?.let { it -> }
                    }
                    is ResultRequest.ErrorConnection -> {
                        result.exception.message?.let { it -> }
                    }
                }
            }
        }
    }

    private fun searchGasto(){
        if (position+1 != sizeCount){
            id = listDeputado[position].id.toString()
            nome = listDeputado[position].nome
            position += 1
            observer()
        }
        else toList()
    }

    // Busca gasto por deputado
    private fun observer(){

        val retrofit = Retrofit.createService(ApiServiceIdDespesas::class.java)
        val call: Call<Despesas> = retrofit.getDespesas(id, year.toString(), 100, page)

        call.enqueue(object: Callback<Despesas> {
            override fun onResponse(call: Call<Despesas>, despesas: Response<Despesas>){
                val despesa = despesas.body()
                if (despesa?.dados?.isNotEmpty() == true){
                    page += 1
                    listGasto += despesa.dados
                    val size = despesa.dados.size
                    numberNote += size
                    if (size >= 100) {
                        observer()
                    }
                    else {
                        if (year != 2014) {
                            page = 1
                            year -= 1
                            observer()
                        }
                        else {
                            year = 2022
                            calculandoNotas()
                            searchGasto()
                        }
                    }
                }
                else{
                    if (year != 2014) {
                        page = 1
                        year = 2022
                        calculandoNotas()
                        searchGasto()
                    }
                }
            }

            override fun onFailure(call: Call<Despesas>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun calculandoNotas(){
        listGasto.forEach {
            gastoPorDeputado += it.valorDocumento.toInt()
        }
        listNomeGasto.add(NomeGastoTotal(nome, gastoPorDeputado))
        gastoPorDeputado = 0
    }

    private fun toList() {

        var ano2015 = 0
        var ano2016 = 0
        var ano2017 = 0
        var ano2018 = 0
        var ano2019 = 0
        var ano2020 = 0
        var ano2021 = 0
        var ano2022 = 0

        var manutencao = 0
        var combustivel = 0
        var passagens = 0
        var divulgacao = 0
        var telefonia = 0
        var servicos = 0
        var alimentacao = 0
        var outros = 0

        var totalGeral = 0

        listGasto.forEach{

            val valor = it.valorDocumento.toInt()
            totalGeral += valor

            when (it.tipoDespesa.substring(0,5)){
                "MANUT" -> manutencao += valor
                "COMBU" -> combustivel += valor
                "PASSA" -> passagens += valor
                "DIVUL" -> divulgacao += valor
                "TELEF" -> telefonia += valor
                "SERVI" -> servicos += valor
                "FORNE" -> alimentacao += valor
                else -> outros += valor
            }
            when (year) {
                2022 -> ano2022 += valor
                2021 -> ano2021 += valor
                2020 -> ano2020 += valor
                2019 -> ano2019 += valor
                2018 -> ano2018 += valor
                2017 -> ano2017 += valor
                2016 -> ano2016 += valor
                2015 -> ano2015 += valor
            }
        }
    }
}