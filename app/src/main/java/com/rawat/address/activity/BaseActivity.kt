package com.rawat.address.activity

import androidx.activity.ComponentActivity
import com.rawat.address.AddressApp
import org.kodein.di.DI

open class BaseActivity: ComponentActivity() {
    val di: DI by lazy { (application as AddressApp).di }
}