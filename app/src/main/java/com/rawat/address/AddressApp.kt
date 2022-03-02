package com.rawat.address

import android.app.Application
import android.content.Context
import com.library.address.di.*
import org.kodein.di.*

class AddressApp: Application(), DIAware {
    override val di: DI = initKodein {
        it.bindSingleton<Context> { this@AddressApp }
    }
}