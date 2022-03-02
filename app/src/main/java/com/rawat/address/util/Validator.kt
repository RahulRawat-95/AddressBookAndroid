package com.rawat.address.util

private const val REQUIRED_MESSAGE = "This field is required"
private const val LENGTH_MESSAGE = "Length is wrong"

sealed interface Validator
open class Required(var message: String = REQUIRED_MESSAGE): Validator
open class Length(var length: Int, var message: String = LENGTH_MESSAGE): Validator