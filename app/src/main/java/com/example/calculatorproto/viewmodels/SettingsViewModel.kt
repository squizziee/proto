package com.example.calculatorproto.viewmodels

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.CustomTheme
import com.example.calculatorproto.misc.CustomThemeData
import com.example.calculatorproto.misc.FirestoreAccessor
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val database = FirestoreAccessor()
    var currentC1: Int? = null
    var currentC2: Int? = null
    var currentC3: Int? = null
    var currentC4: Int? = null
    var currentC5: Int? = null
    var currentC6: Int? = null
    var currentC7: Int? = null

    fun addCustomTheme(uid: String, fallback: ColorScheme/*, appliedEarlier: CustomTheme*/) {
        viewModelScope.launch {
            database.addCustomTheme(
                uid,
                CustomThemeData(
                    primary =
                        if (currentC1 == -1) {
                            fallback.primary.toArgb()
                        } else {
                            currentC1
                        },
                    onPrimary =
                        if (currentC2 == -1) {
                            fallback.onPrimary.toArgb()
                        } else {
                            currentC2
                        },
                    primaryContainer =
                        if (currentC3 == -1) {
                            fallback.primaryContainer.toArgb()
                        } else {
                            currentC3
                        },
                    onPrimaryContainer =
                        if (currentC4 == -1) {
                            fallback.onPrimaryContainer.toArgb()
                        } else {
                            currentC4
                        },
                    secondary =
                        if (currentC5 == -1) {
                            fallback.secondary.toArgb()
                        } else {
                            currentC5
                        },
                    onSecondary =
                        if (currentC6 == -1) {
                            fallback.onSecondary.toArgb()
                        } else {
                            currentC6
                        },
                    surface =
                        if (currentC7 == -1) {
                            fallback.surface.toArgb()
                        } else {
                            currentC7
                        },
                )
            )
        }
    }

}