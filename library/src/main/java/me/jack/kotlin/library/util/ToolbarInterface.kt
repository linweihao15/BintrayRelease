package me.jack.kotlin.library.util

import android.graphics.drawable.Drawable
import android.support.annotation.MenuRes
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.jack.kotlin.library.extension.ctx

/**
 * Created by Jack on 2018/1/7.
 */
interface ToolbarInterface {
    val toolbar: Toolbar

    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.title = value
        }

    var toolbarSubTitle: String
        get() = toolbar.subtitle.toString()
        set(value) {
            toolbar.subtitle = value
        }

    fun showToolbarBackBtn(icon: Drawable = createBackIcon(), back:() -> Unit) {
        toolbar.navigationIcon = icon
        toolbar.setNavigationOnClickListener { back() }
    }

    private fun createBackIcon() = DrawerArrowDrawable(toolbar.ctx).apply { progress = 1f }

    fun setupMenu(@MenuRes menuRes: Int, itemClick: (item: MenuItem) -> Unit) {
        toolbar.inflateMenu(menuRes)
        toolbar.setOnMenuItemClickListener {
            itemClick(it)
            true
        }
        if (toolbar.menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val builder = toolbar.menu as MenuBuilder
                builder.setOptionalIconsVisible(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}