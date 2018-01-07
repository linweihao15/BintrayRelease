package me.jack.kotlinlibrary.extension

import kotlin.reflect.KProperty

/**
 * Created by Jack on 2018/1/7.
 */
object DelegatesExtensions {
    fun <T> notNullSingleValue() = NotNullSingleValue<T>()
}

class NotNullSingleValue<T> {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            value ?: throw IllegalStateException("${property.name} not initialized.")

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value else throw IllegalStateException("${property.name} already initialized.")
    }
}