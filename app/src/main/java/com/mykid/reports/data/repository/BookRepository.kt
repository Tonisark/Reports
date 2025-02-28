package com.mykid.reports.data.repository

import android.content.Context
import com.mykid.reports.data.storage.SharedPreferencesManager

class BookRepository(private val context: Context) {
    private val sharedPreferences = SharedPreferencesManager(context)

    fun saveBooks(books: List<String>) {
        sharedPreferences.saveStringList("books", books)
    }

    fun loadBooks(): List<String> {
        return sharedPreferences.getStringList("books")
    }
} 