package com.example.freefoodfinder.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.freefoodfinder.R

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private val prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val TOKEN_KEY = "token"
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains(TOKEN_KEY)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun logout() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }
}
