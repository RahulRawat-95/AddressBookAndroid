package com.rawat.address.model

import com.library.address.database.Address
import com.library.address.repository.CrudState
import java.io.Serializable

class AddressSerialized(
    val id: Long,
    var firstname: String?,
    var lastname: String?,
    var address1: String,
    var address2: String?,
    var city: String,
    var zipcode: String,
    var phone: String?,
    var state_name: String?,
    var alternative_phone: String?,
    var company: String?,
    val state_id: Long,
    val country_id: Long,
    var is_default: Boolean?
) : Serializable {
    constructor(address: Address) : this(
        id = address.id,
        firstname = address.firstname,
        lastname = address.lastname,
        address1 = address.address1,
        address2 = address.address2,
        city = address.city,
        zipcode = address.zipcode,
        phone = address.phone,
        state_name = address.state_name,
        alternative_phone = address.alternative_phone,
        company = address.company,
        state_id = address.state_id,
        country_id = address.country_id,
        is_default = address.is_default
    )

    fun toAddress(): Address {
        return Address(
            id = id,
            firstname = firstname,
            lastname = lastname,
            address1 = address1,
            address2 = address2,
            city = city,
            zipcode = zipcode,
            phone = phone,
            state_name = state_name,
            alternative_phone = alternative_phone,
            company = company,
            state_id = state_id,
            country_id = country_id,
            crud_state = CrudState.CREATE,
            is_default = is_default
        )
    }
}