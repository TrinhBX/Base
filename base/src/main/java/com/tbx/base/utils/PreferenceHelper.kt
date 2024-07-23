package com.tbx.base.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tbx.base.model.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 9/3/23
 **/

class PreferenceHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()
    companion object {
        private const val SHARED_PREFERENCE = "SHARED_PREFERENCE"

        val listLanguage = arrayListOf(
            Language("English", "en"),
            Language("Vietnamese", "vi"),
//            Language("French", "fr"),
//            Language("German", "de"),
//            Language("Russian", "ru"),
//            Language("Spanish", "es"),
//            Language("Portuguese", "pt")
        )
        private const val LANGUAGE = "LANGUAGE"
        private val LANGUAGE_DEFAULT = listLanguage[0]
    }
    private fun save(key: String, value: Any) {
        val editor = sharedPreferences.edit()
        when (value) {
            is Boolean -> {
                editor.putBoolean(key, value)
            }

            is Int -> {
                editor.putInt(key, value)
            }

            is Float -> {
                editor.putFloat(key, value)
            }

            is Long -> {
                editor.putLong(key, value)
            }

            else -> {
                editor.putString(key, value.toString())
            }
        }
        editor.apply()
    }
    var language: Language
        get() {
            val value = sharedPreferences.getString(LANGUAGE, Locale.getDefault().language)
            return try {
                listLanguage.first {
                    it.code == value
                }
            } catch (e: Exception) {
                sharedPreferences.edit().putString(LANGUAGE, LANGUAGE_DEFAULT.code).apply()
                LANGUAGE_DEFAULT
            }
        }
        set(language) {
            sharedPreferences.edit().putString(LANGUAGE, language.code).apply()
        }
}