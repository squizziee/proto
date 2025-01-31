package com.example.calculatorproto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.CustomThemeData
import com.example.calculatorproto.misc.FirestoreAccessor
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val database = FirestoreAccessor()
    var currentA: String? = null
    var currentB: String? = null
    var currentC: String? = null

    fun addCustomTheme(uid: String) {
        viewModelScope.launch {
            database.addCustomTheme(
                uid,
                CustomThemeData(
                    currentA,
                    currentB,
                    currentC
                )
            )
        }
    }

}