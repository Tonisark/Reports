package com.mykid.reports

import android.content.Context

object BookStorage {
    private const val PREF_NAME = "book_preferences"
    private const val BOOKS_KEY = "saved_books"

    fun saveBooks(context: Context, books: List<String>) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val booksJson = books.joinToString(",")
        sharedPreferences.edit().putString(BOOKS_KEY, booksJson).apply()
    }

    fun loadBooks(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val booksJson = sharedPreferences.getString(BOOKS_KEY, "")
        return if (booksJson.isNullOrEmpty()) {
            emptyList()
        } else {
            booksJson.split(",")
        }
    }
}