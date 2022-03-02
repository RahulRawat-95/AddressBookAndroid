package com.rawat.address.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.library.address.database.Address
import com.library.address.repository.CrudState
import com.rawat.address.model.AddressSerialized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddUpdateViewModel(application: Application) : BaseViewModel(application) {
    val showProgressBar: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val successfullySubmitted: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun addOrUpdateAddress(
        addressSerialized: AddressSerialized?,
        map: Map<String, String>,
        isDefault: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgressBar.emit(true)
            if (addressSerialized != null) {
                repo.updateAddress(
                    Address(
                        id = addressSerialized.id,
                        firstname = map[KEY_FIRSTNAME],
                        lastname = map[KEY_LASTNAME],
                        address1 = map[KEY_ADDRESS_1] ?: "",
                        address2 = map[KEY_ADDRESS_2],
                        city = map[KEY_CITY] ?: "",
                        zipcode = map[KEY_ZIPCODE] ?: "",
                        phone = map[KEY_PHONE],
                        state_name = map[KEY_STATENAME],
                        alternative_phone = map[KEY_ALT_PHONE],
                        company = map[KEY_COMPANY],
                        state_id = addressSerialized.state_id,
                        country_id = addressSerialized.country_id,
                        is_default = isDefault,
                        crud_state = CrudState.UPDATE
                    )
                )
            } else {
                repo.createAddress(
                    Address(
                        id = 0,
                        firstname = map[KEY_FIRSTNAME],
                        lastname = map[KEY_LASTNAME],
                        address1 = map[KEY_ADDRESS_1] ?: "",
                        address2 = map[KEY_ADDRESS_2],
                        city = map[KEY_CITY] ?: "",
                        zipcode = map[KEY_ZIPCODE] ?: "",
                        phone = map[KEY_PHONE],
                        state_name = map[KEY_STATENAME],
                        alternative_phone = map[KEY_ALT_PHONE],
                        company = map[KEY_COMPANY],
                        state_id = 1,
                        country_id = 2,
                        is_default = isDefault,
                        crud_state = CrudState.CREATE
                    )
                )
            }
            showProgressBar.emit(false)
            successfullySubmitted.emit(true)
        }
    }

    companion object {
        const val KEY_FIRSTNAME = "firstname"
        const val KEY_LASTNAME = "lastname"
        const val KEY_ADDRESS_1 = "address1"
        const val KEY_ADDRESS_2 = "address2"
        const val KEY_CITY = "city"
        const val KEY_ZIPCODE = "zipcode"
        const val KEY_PHONE = "phoen"
        const val KEY_STATENAME = "statename"
        const val KEY_ALT_PHONE = "altphone"
        const val KEY_COMPANY = "company"
    }
}