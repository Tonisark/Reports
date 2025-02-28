package com.mykid.reports.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T : UiState>(initialState: T) : ViewModel() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<T> = _uiState.asStateFlow()

    protected fun updateState(update: (T) -> T) {
        _uiState.value = update(_uiState.value)
    }

    protected fun setLoading(loading: Boolean) {
        updateState { currentState -> 
            currentState.updateLoading(loading) as T
        }
    }

    protected fun setError(error: String?) {
        updateState { currentState ->
            currentState.updateError(error) as T
        }
    }
}
