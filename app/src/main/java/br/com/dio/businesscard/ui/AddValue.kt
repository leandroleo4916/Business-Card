package br.com.dio.businesscard.ui

import android.os.Environment
import br.com.dio.businesscard.ui.dataclass.NomeGastoTotal
import br.com.dio.businesscard.ui.dataclass.Senador
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer
import java.util.regex.Pattern

class AddValue {

    val totalGeral = """"totalGeral""""
    val notasGeral = """"totalNotas""""
    val aluguelGeral = """"aluguel""""
    val divulgacaoGeral = """"divuldacao""""
    val passagensGeral = """"passagens""""
    val contratacaoGeral = """"contratacao""""
    val locomocaoGeral = """"locomocao""""
    val aquisicaoGeral = """"aquisicao""""
    val outrosGeral = """"outros""""

    private val getEncodeString = EncodeString()
    private var listTotalGeral = ""

    var ano2015 = 0
    var ano2016 = 0
    var ano2017 = 0
    var ano2018 = 0
    var ano2019 = 0
    var ano2020 = 0
    var ano2021 = 0
    var ano2022 = 0

    var aluguel = 0
    var divulgacao = 0
    var passagens = 0
    var contratacao = 0
    var locomocao = 0
    var aquisicao = 0
    var servico = 0
    var outros = 0

    var valorTotalAno = 0
    var totalNotas = 0
    val listNomeGasto: ArrayList<NomeGastoTotal> = arrayListOf()
    var listNomePrint: ArrayList<String> = arrayListOf()

    fun process(listComplet: ArrayList<Senador>) {

        totalNotas = listComplet.size
        listComplet.forEach {

            val valor = it.valor.toInt()
            val nome = it.nome
            valorTotalAno += valor

            when (it.ano) {
                "2015" -> { ano2015 += valor }
                "2016" -> { ano2016 += valor }
                "2017" -> { ano2017 += valor }
                "2018" -> { ano2018 += valor }
                "2019" -> { ano2019 += valor }
                "2020" -> { ano2020 += valor }
                "2021" -> { ano2021 += valor }
                "2022" -> { ano2022 += valor }
            }
            when (it.tipo.substring(0,5)){
                "Alugu" -> aluguel += valor
                "Divul" -> divulgacao += valor
                "Passa" -> passagens += valor
                "Contr" -> contratacao += valor
                "Locom" -> locomocao += valor
                "Aquis" -> aquisicao += valor
                "Servi" -> servico += valor
                else -> outros += valor
            }
            if (listNomeGasto.isEmpty()) {
                listNomeGasto.add(NomeGastoTotal(nome, valor))
            }
            else if (listNomeGasto.isNotEmpty()){
                var addV = 0
                listNomeGasto.forEach { valorLista ->
                    if (valorLista.nome == nome){
                        valorLista.gasto += valor
                        addV = 1
                    }
                }
                if (addV == 0){
                    listNomeGasto.add(NomeGastoTotal(nome, valor))
                }
            }
        }
        addValue()
    }

    private fun addValue() {

        listNomeGasto.forEach {
            val nome = converterNome(it.nome)
            listNomePrint.add("""{"nome":"$nome", "gasto":"${it.gasto}"}""")
        }

        listTotalGeral = """{"gastoGeral": [{$totalGeral:"$valorTotalAno", $notasGeral:"$totalNotas", 
             $aluguelGeral:"$aluguel", $divulgacaoGeral:"$divulgacao", $passagensGeral:"$passagens", 
             $contratacaoGeral:"$contratacao", $locomocaoGeral:"$locomocao", $aquisicaoGeral:"$aquisicao",
             $outrosGeral:"$outros", {"listSenador": $listNomePrint}}]}"""

        rec()
    }

    private fun rec() {
        try {
            val arq = File(Environment.getExternalStorageDirectory(), "/gastoGeral/geral")
            val fos = FileOutputStream(arq)
            fos.write(listTotalGeral.toByteArray())
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

    private fun converterNome(nome: String): String {
        val s = nome.split(" ")
        var nomeP = ""
        s.forEach {
            val size = it.length
            nomeP +=
                if (it == "DE" || it == "DA"){
                    val i = it.lowercase()
                    "$i "
                } else {
                    val i = it.substring(0,1)
                    val k = it.substring(1,size).lowercase()
                    if (nome == "") { i+k
                    } else { "$i$k " }
                }
        }
        return nomeP
    }
}