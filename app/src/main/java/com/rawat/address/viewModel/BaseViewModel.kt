package com.rawat.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.library.address.repository.Repo
import com.rawat.address.AddressApp
import org.kodein.di.DI
import org.kodein.di.instance

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    val di: DI by lazy { (application as AddressApp).di }

    protected val repo: Repo by di.instance()
}