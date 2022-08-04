package br.com.dio.businesscard.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import br.com.dio.businesscard.App
import br.com.dio.businesscard.R
import br.com.dio.businesscard.data.BusinessCard
import br.com.dio.businesscard.databinding.ActivityAddBusinessCardBinding
import br.com.dio.businesscard.databinding.SelectColorBinding

class AddBusinessCardActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddBusinessCardBinding.inflate(layoutInflater) }
    private val bindingColor by lazy { SelectColorBinding.inflate(layoutInflater) }
    private lateinit var color: String

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
        clickColor()
    }

    private fun insertListeners() {
        binding.tilCor.editText?.doAfterTextChanged {
            createSelectColor()
        }
        binding.btnConfirm.setOnClickListener {
            val nome = binding.tilNome.editText?.text.toString()
            val empresa = binding.tilEmpresa.editText?.text.toString()
            val telefone = binding.tilTelefone.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val fundoPersonalizado = binding.tilCor.editText?.text.toString()

            if (nome.isEmpty() || empresa.isEmpty() || telefone.isEmpty() ||
                    email.isEmpty() || fundoPersonalizado.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
            else {
                val businessCard = BusinessCard(
                    nome = nome,
                    empresa = empresa,
                    telefone = telefone,
                    email = email,
                    fundoPersonalizado = fundoPersonalizado
                )
                mainViewModel.insert(businessCard)
                Toast.makeText(this, R.string.label_show_success, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun createSelectColor(){
        val inflate = layoutInflater.inflate(R.layout.select_color, null)
        val dialog = createDialog("Selecione a cor")
        dialog.setView(inflate)
        dialog.setPositiveButton(getString(R.string.selecionar)) { _, _ ->
            binding.tilCor.editText?.text ?: color
        }
    }

    private fun createDialog(message: String): AlertDialog.Builder {
        val builder = this.let { AlertDialog.Builder(it) }
        builder.setTitle(message)
        builder.setCancelable(false)
        return builder
    }

    private fun clickColor() {
        bindingColor.run {
            colorOne.setOnClickListener { selectColor(it) }
            colorTwo.setOnClickListener { color = it.contentDescription.toString() }
            colorThree.setOnClickListener { color = it.contentDescription.toString() }
            colorFour.setOnClickListener { color = it.contentDescription.toString() }
            colorFive.setOnClickListener { color = it.contentDescription.toString() }
            colorSix.setOnClickListener { color = it.contentDescription.toString() }
            colorSeven.setOnClickListener { color = it.contentDescription.toString() }
            colorEight.setOnClickListener { color = it.contentDescription.toString() }
            colorNine.setOnClickListener { color = it.contentDescription.toString() }
            colorTen.setOnClickListener { color = it.contentDescription.toString() }
            colorEleven.setOnClickListener { color = it.contentDescription.toString() }
            colorTwelve.setOnClickListener { color = it.contentDescription.toString() }
            colorThirteen.setOnClickListener { color = it.contentDescription.toString() }
            colorFourteen.setOnClickListener { color = it.contentDescription.toString() }
            colorFifteen.setOnClickListener { color = it.contentDescription.toString() }
        }
    }

    private fun selectColor(it: View) {
        color = it.contentDescription.toString()
        Toast.makeText(this, "Cor selecionada!", Toast.LENGTH_SHORT).show()
    }
}
