package br.com.dio.businesscard.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.dio.businesscard.App
import br.com.dio.businesscard.R
import br.com.dio.businesscard.data.BusinessCard
import br.com.dio.businesscard.databinding.ActivityAddBusinessCardBinding

class AddBusinessCardActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddBusinessCardBinding.inflate(layoutInflater) }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
    }

    private fun insertListeners() {
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
}
