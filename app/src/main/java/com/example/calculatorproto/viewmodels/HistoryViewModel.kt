package com.example.calculatorproto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorproto.misc.FirestoreAccessor
import com.example.calculatorproto.misc.History
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel: ViewModel() {
    private val firestore = FirestoreAccessor()
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    var history: History? = null

    fun loadHistory (uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            history = firestore.getHistoryEntries(uid)
            _isLoading.value = false
        }
    }
}