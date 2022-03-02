package com.rawat.address.composable

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import com.rawat.address.util.Length
import com.rawat.address.util.Required
import com.rawat.address.util.Validator

class Field(
    val name: String,
    val label: String = "",
    val initialText: String? = null,
    val validators: List<Validator>
) {
    var text: String by mutableStateOf(initialText ?: "")
    var lbl: String by mutableStateOf(label)
    var error: String by mutableStateOf("")
    var hasError: Boolean by mutableStateOf(false)

    fun clear() {
        text = ""
        error = ""
    }

    private fun showError(error: String) {
        hasError = true
        this.error = error
    }

    private fun hideError() {
        hasError = false
        this.error = ""
    }

    @Composable
    fun Content(modifier: Modifier, scrollTo: (Float) -> Unit) {
        var scrollToPosition = 0.0F

        Column(modifier = Modifier.onGloballyPositioned {
            scrollToPosition = it.positionInParent().y
        }) {
            TextField(
                value = text,
                isError = hasError,
                label = { Text(text = lbl) },
                modifier = modifier.onFocusChanged {
                    if (it.isFocused)
                        scrollTo(scrollToPosition)
                },
                onValueChange = { value ->
                    hideError()
                    text = value
                }
            )
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = modifier
            )
        }
    }

    fun validate(): Boolean {
        return validators.map {
            when (it) {
                is Required -> {
                    if (text.isEmpty()) {
                        showError(it.message)
                        return@map false
                    }
                    true
                }
                is Length -> {
                    if (text.length < it.length) {
                        showError(it.message)
                        return@map false
                    }
                    true
                }
            }
        }.all { it }
    }
}