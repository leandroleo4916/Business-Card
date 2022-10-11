package br.com.dio.businesscard.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.dio.businesscard.ui.dataclass.MainDataClass
import br.com.dio.businesscard.ui.repository.ResultRequest
import br.com.dio.businesscard.ui.repository.SearchRepository

class CamaraViewModel(private val repository: SearchRepository): ViewModel() {

    fun searchData(ordenarPor: String):
            LiveData<ResultRequest<MainDataClass?>> = repository.searchData(ordenarPor)

}

