package com.example.budgy.ui.helper

import android.content.Context
import android.util.Log

object PreferenceHelper {
    private const val PREFS_NAME = "user_prefs"
    private const val IS_LOGGED_IN = "is_logged_in"
    private const val USER_NAME = "user_name"
    private const val TOKEN = "token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(TOKEN, token).apply()
        Log.d("PreferenceHelper", "Token disimpan: $token")
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(TOKEN, null)
        Log.d("PreferenceHelper", "Token diambil: $token")
        return token
    }

    fun saveIsLoggedIn(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
        Log.d("PreferenceHelper", "Status login disimpan: $isLoggedIn") // Tambahkan log
    }

    fun getIsLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(IS_LOGGED_IN, false) // Default false jika tidak ada data
        Log.d("PreferenceHelper", "Status login diambil: $isLoggedIn") // Tambahkan log
        return isLoggedIn
    }

    fun saveUserName(context: Context, userName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(USER_NAME, userName).apply()
        Log.d("PreferenceHelper", "Nama pengguna disimpan: $userName") // Tambahkan log
    }

    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userName = prefs.getString(USER_NAME, "User") // Default "User" jika nama tidak ditemukan
        Log.d("PreferenceHelper", "Nama pengguna diambil: $userName") // Tambahkan log
        return userName
    }
}
