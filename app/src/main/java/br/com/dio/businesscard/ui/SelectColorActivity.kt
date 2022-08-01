package br.com.dio.businesscard.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.dio.businesscard.databinding.SelectColorBinding


class SelectColorActivity : AppCompatActivity() {

    private val binding by lazy { SelectColorBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        clickColor()
    }

    private fun clickColor() {
        binding.run {
            colorOne.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorTwo.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorThree.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorFour.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorFive.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorSix.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorSeven.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorEight.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorNine.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorTen.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorEleven.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorTwelve.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorThirteen.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorFourteen.setOnClickListener { getColorCard(it.contentDescription.toString()) }
            colorFifteen.setOnClickListener { getColorCard(it.contentDescription.toString()) }
        }
    }

    private fun getColorCard(color: String) {
        val bundle = Bundle()
        bundle.putString("color", color)
        finish()
    }

}