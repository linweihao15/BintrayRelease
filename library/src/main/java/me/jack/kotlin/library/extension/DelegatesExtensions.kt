package me.jack.kotlin.library.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

/**
 * Created by Jack on 2018/1/7.
 */
object DelegatesExtensions {
    fun <T> notNullSingleValue() = NotNullSingleValue<T>()
    fun <T> preferences(ctx: Context, prefsName: String, key: String, defaultValue: T)
            = Preferences(ctx, prefsName, key, defaultValue)
}

class NotNullSingleValue<T> {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            value ?: throw IllegalStateException("${property.name} not initialized.")

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value else throw IllegalStateException("${property.name} already initialized.")
    }
}

class Preferences<T>(private val ctx: Context, private val prefsName: String, private val key: String, private val defaultValue: T) {

    private val prefs: SharedPreferences by lazy {
        ctx.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = findPreference(key, defaultValue)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putPreference(key, value)

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T) = with(prefs) {
        val result: Any = when (default) {
            is Int -> getInt(name, default)
            is Float -> getFloat(name, default)
            is Long -> getLong(name, default)
            is Boolean -> getBoolean(name, default)
            is String -> getString(name, default)
            else -> throw IllegalArgumentException("This type can't be got from Preferences")
        }
        result as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Float -> putFloat(name, value)
            is Long -> putLong(name, value)
            is Boolean -> putBoolean(name, value)
            is String -> putString(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }

}