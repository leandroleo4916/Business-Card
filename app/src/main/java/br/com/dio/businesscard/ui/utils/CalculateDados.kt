package br.com.dio.businesscard.ui.utils

import android.os.Environment
import br.com.dio.businesscard.ui.dataclass.Senador
import java.io.*

class CalculateDados {

    private val getEncodeString = EncodeString()
    private val calculoGeralSenador: CalculoGeralSenador = CalculoGeralSenador()
    private val listComplet: ArrayList<Senador> = arrayListOf()
    private var anoSoma = 2015

    fun searchTotal() {

        while (anoSoma != 2023){
            try {
                val arq = File(Environment.getExternalStorageDirectory(), "$anoSoma.txt")
                val br = BufferedReader(InputStreamReader(FileInputStream(arq), getEncodeString.getEncoding(arq)))

                br.forEachLine {
                    val div = it.split("\";\"")
                    val ano = div[0].split("\"")[1]
                    val mes = if (div[1] == "") "Não foi informado" else div[1]
                    val nome = if (div[2] == "") "Não foi informado" else div[2]
                    val tipo = if (div[3] == "") "Não foi informado" else div[3]
                    val valor = if (div[9] == "") "Não foi informado" else div[9]

                    val value = if (valor.contains(",")){
                        val v = valor.split(",")[0]
                        if (v != "") v
                        else "0"
                    }
                    else valor
                    listComplet.add(Senador(ano, mes, nome, tipo, value))
                }
                anoSoma += 1
                if (anoSoma == 2023) calculoGeralSenador.process(listComplet)

            } catch (e: java.lang.Exception) { }
        }
    }
}