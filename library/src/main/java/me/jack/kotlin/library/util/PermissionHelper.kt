package me.jack.kotlin.library.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by Jack on 2018/3/31.
 */
class PermissionHelper {

    companion object {
        val instance by lazy { PermissionHelper() }
        const val REQUEST_CODE = 8888
    }

    private val mPermissions = ArrayList<String>()
    private var success: (Array<out String>) -> Unit = { emptyArray<String>() }
    private var failure: (Array<out String>, IntArray) -> Unit = fun(_: Array<out String>, _: IntArray) = Unit
    private var end = { _: Array<out String>, _: IntArray -> Unit }
    private var showRationaleCallback = { false }

    fun requestPermissions(vararg permissions: String): PermissionHelper {
        permissions.forEach { mPermissions.add(it) }
        return this
    }

    fun run(activity: Activity) {
        if (mPermissions.isEmpty()) {
            reset()
            return
        }
        val permissions = mPermissions.toTypedArray()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !checkPermission(activity, permissions)) {
            if (shouldShowRequestPermissionRationale(activity, permissions)) {
                if (showRationaleCallback()) {
                    reset()
                    return
                }
            }
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
        } else {
            success(permissions)
            val array = IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }
            end(permissions, array)
            reset()
        }

    }

    fun onSuccess(callback: (permissions: Array<out String>) -> Unit): PermissionHelper {
        success = callback
        return this
    }

    fun onFailure(callback: (permissions: Array<out String>, result: IntArray) -> Unit): PermissionHelper {
        failure = callback
        return this
    }

    fun onEnd(callback: (permissions: Array<out String>, result: IntArray) -> Unit): PermissionHelper {
        end = callback
        return this
    }

    fun handleShowRationale(callback: () -> Boolean): PermissionHelper {
        showRationaleCallback = callback
        return this
    }

    fun handlePermissionsResult(permissions: Array<out String>, result: IntArray) {
        val state = result.none { it != PackageManager.PERMISSION_GRANTED }
        when (state) {
            true -> success(permissions)
            false -> failure(permissions, result)
        }
        end(permissions, result)
        reset()
    }

    private fun checkPermission(ctx: Context, permissions: Array<String>): Boolean {
        return permissions.none { ContextCompat.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED }
    }

    private fun shouldShowRequestPermissionRationale(activity: Activity, permissions: Array<out String>): Boolean {
        return permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
    }

    private fun reset() {
        mPermissions.clear()
        success = { emptyArray<String>() }
        failure = fun(_: Array<out String>, _: IntArray) = Unit
        end = { _: Array<out String>, _: IntArray -> Unit }
        showRationaleCallback = { false }
    }
}