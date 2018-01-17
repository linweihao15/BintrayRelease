package me.jack.bintrayrelease.security

import android.content.Context
import me.jack.bintrayrelease.App
import me.jack.kotlin.library.extension.DelegatesExtensions
import me.jack.kotlin.library.util.SecurityUtils

/**
 * Created by Jack on 2018/1/17.
 */
class SecurityHelper private constructor(private val ctx: Context = App.instance) {

    companion object {
        val instance by lazy { SecurityHelper() }
        private val PREF_NAME = "security"
        private val USER = "user"
        private val USER_KEY = "user_key"
        private val DEFAULT_VALUE = "none"
    }

    private var user: String by DelegatesExtensions.preferences(ctx, PREF_NAME, USER, DEFAULT_VALUE)
    private var userKey: String by DelegatesExtensions.preferences(ctx, PREF_NAME, USER_KEY, DEFAULT_VALUE)

    fun getUserName(): String {
        if (user == DEFAULT_VALUE || userKey == DEFAULT_VALUE) {
            return DEFAULT_VALUE
        }
        SecurityUtils.initRsaKeyPair(ctx, USER)
        val decryptedKey = SecurityUtils.decryptWithRsa(userKey, USER)
        return SecurityUtils.decryptWithAes(user, decryptedKey)
    }

    fun setUserName(name: String) {
        SecurityUtils.initRsaKeyPair(ctx, USER)
        val k = SecurityUtils.generateSecretKey()
        userKey = SecurityUtils.encryptWithRsa(k.encoded, USER)
        user = SecurityUtils.encryptWithAes(name, k.encoded)
    }

    fun deleteUserName() {
        SecurityUtils.deleteEntry(USER)
        userKey = DEFAULT_VALUE
        user = DEFAULT_VALUE
    }

}