package com.rawat.address.extensions

import java.lang.StringBuilder

fun Array<out String?>.concatenate(separator: String = " "): String {
    val stringBuilder = StringBuilder("")

    this.forEachIndexed { index, item ->
        if (!item.isNullOrBlank()) {
            stringBuilder.append(item)
            if (index < size - 1) {
                stringBuilder.append(separator)
            }
        }
    }

    return stringBuilder.toString()
}