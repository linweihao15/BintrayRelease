package me.jack.bintrayrelease

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import me.jack.kotlin.library.extension.ensureNotBlank
import me.jack.kotlin.library.extension.highLight
import me.jack.kotlin.library.extension.translucentStatus
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), ToolbarInterface {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.translucentStatus(resources.getColor(R.color.colorPrimaryDark))
        initToolbar()
        highLight()
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.app_name)
        setupMenu(R.menu.menu_main) {
            when (it.itemId) {
                R.id.action_info -> toast(getString(R.string.info))
            }
        }
    }

    private fun highLight() {
        tv.text = "".ensureNotBlank("Hello World").highLight("Hello", resources.getColor(R.color.colorAccent))
    }

}
