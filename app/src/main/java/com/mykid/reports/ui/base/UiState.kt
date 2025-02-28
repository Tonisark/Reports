package com.mykid.reports.ui.base

interface UiState {
    val isLoading: Boolean
    val error: String?

    fun updateLoading(loading: Boolean): UiState
    fun updateError(error: String?): UiState
} 