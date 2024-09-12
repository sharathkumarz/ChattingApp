package com.sharathkumar.chattingapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true } // Configuring Json to ignore unknown keys

    companion object{
        private const val KEY = "User"
        private const val KEY_PREFIX = "chat"
    }

    fun saveUserData(userData: UserProfile){
            val jsonString = json.encodeToString(userData)
            sharedPreferences.edit().putString(KEY, jsonString).apply()
    }

    fun saveUserProfil(userProfile: UserProfile) {
            val jsonString = json.encodeToString(userProfile)
            sharedPreferences.edit().putString(userProfile.username, jsonString).apply()
    }

}