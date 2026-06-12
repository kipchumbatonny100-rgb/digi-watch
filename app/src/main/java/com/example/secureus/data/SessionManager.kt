package com.example.secureus.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Basic Session Manager. 
 * NOTE: In a production environment, use EncryptedSharedPreferences for tokens.
 */
object SessionManager {
    private const val PREF_NAME = "safe_zone_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) = prefs?.edit()?.putString(KEY_TOKEN, value)?.apply() ?: Unit

    var userId: Int
        get() = prefs?.getInt(KEY_USER_ID, -1) ?: -1
        set(value) = prefs?.edit()?.putInt(KEY_USER_ID, value)?.apply() ?: Unit

    var userName: String?
        get() = prefs?.getString(KEY_USER_NAME, null)
        set(value) = prefs?.edit()?.putString(KEY_USER_NAME, value)?.apply() ?: Unit

    var userRole: String?
        get() = prefs?.getString(KEY_USER_ROLE, "user")
        set(value) = prefs?.edit()?.putString(KEY_USER_ROLE, value)?.apply() ?: Unit

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
