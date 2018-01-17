package me.jack.kotlin.library.util

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

/**
 * Created by Jack on 2018/1/16.
 */
object SecurityUtils {

    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val KEY_ALGORITHM_RSA = "RSA"
    private val KEY_ALGORITHM_AES = "AES"
    private val RSA_MODE = "RSA/ECB/PKCS1Padding"
    private val AES_MODE = "AES/CBC/PKCS7Padding"

    fun initRsaKeyPair(ctx: Context, alias: String) {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(alias)) {
            Log.d(javaClass.simpleName, "Alias not exist, create it.")
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 20)
            val spec = generateAlgorithmParameterSpec(ctx, alias, start, end)
            val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
            generator.initialize(spec)
            generator.generateKeyPair()
        }
    }

    fun encryptWithRsa(data: ByteArray, alias: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val entry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val cipher = Cipher.getInstance(RSA_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, entry.certificate.publicKey)
        val bytes = cipher.doFinal(data)
        return encodeToString(bytes)
    }

    fun decryptWithRsa(data: String, alias: String): ByteArray {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val entry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val cipher = Cipher.getInstance(RSA_MODE)
        cipher.init(Cipher.DECRYPT_MODE, entry.privateKey)
        return cipher.doFinal(decodeToBytes(data))
    }

    fun generateSecretKey(): SecretKey {
        val generator = KeyGenerator.getInstance(KEY_ALGORITHM_AES)
        generator.init(256) //Set key size to 256
        return generator.generateKey()
    }

    fun deleteEntry(alias: String) {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
            Log.d(javaClass.simpleName, "Delete entry: $alias")
        }
    }

    fun encryptWithAes(data: String, key: ByteArray): String {
        val iv = generateIV()
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, KEY_ALGORITHM_AES), IvParameterSpec(iv))
        val bytes = cipher.doFinal(data.toByteArray())
        return encodeToString(bytes)
    }

    fun decryptWithAes(data: String, key: ByteArray): String {
        val iv = generateIV()
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, KEY_ALGORITHM_AES), IvParameterSpec(iv))
        val bytes = cipher.doFinal(decodeToBytes(data))
        return String(bytes)
    }

    private fun generateAlgorithmParameterSpec(ctx: Context, alias: String, start: Calendar, end: Calendar): AlgorithmParameterSpec {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val purpose = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            return KeyGenParameterSpec.Builder(alias, purpose)
                    .setCertificateSubject(X500Principal("CN=$alias, O=${ctx.packageName}"))
                    .setCertificateSerialNumber(BigInteger.ONE)
                    .setCertificateNotBefore(start.time)
                    .setCertificateNotAfter(end.time)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) //Must set
                    .build()
        } else {
            return KeyPairGeneratorSpec.Builder(ctx)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=$alias, O=${ctx.packageName}"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
        }
    }

    private fun encodeToString(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun decodeToBytes(str: String): ByteArray {
        return Base64.decode(str, Base64.DEFAULT)
    }

    private fun generateIV(): ByteArray {
        val iv = ByteArray(16)
        iv.fill(0x00)
        return iv
    }

}