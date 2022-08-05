package br.com.dio.businesscard.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.dio.businesscard.App
import br.com.dio.businesscard.R
import br.com.dio.businesscard.data.BusinessCard
import br.com.dio.businesscard.databinding.ActivityAddBusinessCardBinding
import com.google.android.material.button.MaterialButton

class AddBusinessCardActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddBusinessCardBinding.inflate(layoutInflater) }
    private lateinit var color: String

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
    }

    private fun insertListeners() {
        binding.tilCor.setOnClickListener { createSelectColor() }
        binding.btnConfirm.setOnClickListener {
            val nome = binding.tilNome.editText?.text.toString()
            val empresa = binding.tilEmpresa.editText?.text.toString()
            val telefone = binding.tilTelefone.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()

            if (nome.isEmpty() || empresa.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
            else {
                val businessCard = BusinessCard(
                    nome = nome,
                    empresa = empresa,
                    telefone = telefone,
                    email = email,
                    fundoPersonalizado = color
                )
                mainViewModel.insert(businessCard)
                Toast.makeText(this, R.string.label_show_success, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    @SuppressLint("ResourceAsColor")
    private fun createSelectColor(){

        fun selectColor(it: View) {
            color = it.contentDescription.toString()
            toastSelectColor()
        }

        val inflate = layoutInflater.inflate(R.layout.select_color, null)
        val colorOne = instanceColor(inflate, R.id.colorOne)
        val colorTwo = instanceColor(inflate, R.id.colorTwo)
        val colorThree = instanceColor(inflate, R.id.colorThree)
        val colorFour = instanceColor(inflate, R.id.colorFour)
        val colorFive = instanceColor(inflate, R.id.colorFive)
        val colorSix = instanceColor(inflate, R.id.colorSix)
        val colorSeven = instanceColor(inflate, R.id.colorSeven)
        val colorEight = instanceColor(inflate, R.id.colorEight)
        val colorNine = instanceColor(inflate, R.id.colorNine)
        val colorTen = instanceColor(inflate, R.id.colorTen)
        val colorEleven = instanceColor(inflate, R.id.colorEleven)
        val colorTwelve = instanceColor(inflate, R.id.colorTwelve)
        val colorThirteen = instanceColor(inflate, R.id.colorThirteen)
        val colorFourteen = instanceColor(inflate, R.id.colorFourteen)
        val colorFifteen = instanceColor(inflate, R.id.colorFifteen)

        val dialog = createDialog("Selecione a cor")
        dialog.setView(inflate)
        dialog.setPositiveButton("Ok"){_, _ ->
            binding.run {
                tilCor.text = getString(R.string.cor_selecionada)
                tilCor.setBackgroundColor(Color.parseColor(color))
            }
        }
        dialog.setNegativeButton("Cancelar"){_, _ -> }
        dialog.create().show()

        colorOne.setOnClickListener { selectColor(it) }
        colorTwo.setOnClickListener { selectColor(it) }
        colorThree.setOnClickListener { selectColor(it) }
        colorFour.setOnClickListener { selectColor(it) }
        colorFive.setOnClickListener { selectColor(it) }
        colorSix.setOnClickListener { selectColor(it) }
        colorSeven.setOnClickListener { selectColor(it) }
        colorEight.setOnClickListener { selectColor(it) }
        colorNine.setOnClickListener { selectColor(it) }
        colorTen.setOnClickListener { selectColor(it) }
        colorEleven.setOnClickListener { selectColor(it) }
        colorTwelve.setOnClickListener { selectColor(it) }
        colorThirteen.setOnClickListener { selectColor(it) }
        colorFourteen.setOnClickListener { selectColor(it) }
        colorFifteen.setOnClickListener { selectColor(it) }

    }

    private fun createDialog(message: String): AlertDialog.Builder {
        val builder = this.let { AlertDialog.Builder(it) }
        builder.setTitle(message)
        builder.setCancelable(false)
        return builder
    }

    private fun toastSelectColor() {
        Toast.makeText(this, R.string.cor_selecionada, Toast.LENGTH_SHORT).show()
    }

    private fun instanceColor(inflate: View, color: Int): MaterialButton {
        return inflate.findViewById(color)
    }
}
