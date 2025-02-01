package com.example.calculatorproto.viewmodels

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.CustomTheme
import com.example.calculatorproto.misc.FirestoreAccessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {
    private val database = FirestoreAccessor()
    var theme: CustomTheme? = null
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadCustomTheme(uid: String, fallback: ColorScheme) {
        if (theme != null) return
        viewModelScope.launch {
            _isLoading.value = true
            val tmp = database.getCustomTheme(uid)

            if (tmp == null) {
                theme = CustomTheme(
                    primary = fallback.primary,
                    onPrimary = fallback.onPrimary,
                    onPrimaryContainer = fallback.onPrimaryContainer,
                    primaryContainer = fallback.primaryContainer,
                    secondary = fallback.secondary,
                    onSecondary = fallback.onSecondary,
                    surface = fallback.surface,
                )
            } else {
                theme = CustomTheme(
                    primary = Color(tmp.primary!!),
                    onPrimary = Color(tmp.onPrimary!!),
                    onPrimaryContainer = Color(tmp.onPrimaryContainer!!),
                    primaryContainer = Color(tmp.primaryContainer!!),
                    secondary = Color(tmp.secondary!!),
                    onSecondary = Color(tmp.onSecondary!!),
                    surface = Color(tmp.surface!!),
//                    primary =
//                    if (tmp.primary == -1) {
//                        fallback.primary
//                    } else {
//                        Color(tmp.primary!!)
//                    },
//                    onPrimary =
//                    if (tmp.onPrimary == -1) {
//                        fallback.onPrimary
//                    } else {
//                        Color(tmp.onPrimary!!)
//                    },
//                    primaryContainer =
//                    if (tmp.primaryContainer == -1) {
//                        fallback.primaryContainer
//                    } else {
//                        Color(tmp.primaryContainer!!)
//                    },
//                    onPrimaryContainer =
//                    if (tmp.onPrimaryContainer == -1) {
//                        fallback.onPrimaryContainer
//                    } else {
//                        Color(tmp.onPrimaryContainer!!)
//                    },
//                    secondary =
//                    if (tmp.secondary == -1) {
//                        fallback.secondary
//                    } else {
//                        Color(tmp.secondary!!)
//                    },
//                    onSecondary =
//                    if (tmp.onSecondary == -1) {
//                        fallback.onSecondary
//                    } else {
//                        Color(tmp.onSecondary!!)
//                    },
//                    surface =
//                    if (tmp.surface == -1) {
//                        fallback.surface
//                    } else {
//                        Color(tmp.surface!!)
//                    },
                )
            }
            _isLoading.value = false
        }


    }
}