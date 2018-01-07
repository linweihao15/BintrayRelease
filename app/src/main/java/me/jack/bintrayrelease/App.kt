package me.jack.bintrayrelease

import android.app.Application
import me.jack.kotlin.library.extension.DelegatesExtensions

/**
 * Created by Jack on 2018/1/7.
 */
class App : Application() {
    companion object {
        var instance by DelegatesExtensions.notNullSingleValue<App>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}