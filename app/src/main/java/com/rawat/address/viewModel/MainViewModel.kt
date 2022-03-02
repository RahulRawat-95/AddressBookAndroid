package com.rawat.address.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.library.address.database.Address
import com.library.address.repository.CFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): BaseViewModel(application) {
    var pauseSyncing = false
    val showProgressBar: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val addressFlow: CFlow<List<Address>> = repo.fetchAddressFlow()!!

    fun startFetching() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                showProgressBar.emit(true)
                repo.fetchAddresses()
                showProgressBar.emit(false)
                delay(60 * 1000)
            }
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgressBar.emit(true)
            repo.deleteAddress(id = address.id)
            showProgressBar.emit(false)
        }
    }

    fun sync() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (!pauseSyncing)
                    repo.sync()
                delay(2 * 60 * 1000)
            }
        }
    }
}