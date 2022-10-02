package br.com.dio.businesscard.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.dio.businesscard.databinding.ActivityMainBinding
import java.io.*
import java.text.Normalizer
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val getEncodeString = EncodeString()
    private var sizeTotal = 0
    private var anoSoma = 2022

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
                searchDoc()
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
                list.add(if (div[10]== "") "Não foi informado" else div[10].split("\",")[0])

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
                    listC =
                        """{$anoV:$ano, $mesV:$mes, $senadorV:$senador, $tipoDespesaV:$tipoDespesa, 
                    $cnpjCpfV:$cnpjCpf, $fornecedorV:$fornecedor, $documentoV:$documento, 
                    $dataV:$data, $detalhamentoV:$detalhamento, $valorReembolsadoV:$valorReembolsado, 
                    $codDocumentoV:$codDocumento}"""
                    listD.add(listC)
                    if (position == size) {
                        rec(listD.toString(), name)
                        anoSoma += 1
                        if (anoSoma != 2023){
                            searchDoc()
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
            fos.write("{\"gastosSenador\": $text".toByteArray())
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
}