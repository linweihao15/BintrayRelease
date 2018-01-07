package me.jack.kotlinlibrary.extension

import android.support.annotation.ColorInt
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

/**
 * Created by Jack on 2018/1/7.
 */
fun CharSequence.highLight(keyword: String, @ColorInt color: Int): SpannableString {
    val span = SpannableString(this)
    val pattern = Pattern.compile(keyword.toLowerCase())
    val matcher = pattern.matcher(span.toString().toLowerCase())
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return span
}

fun CharSequence.ensureNotBlank(c: CharSequence) = if (isNotBlank()) this else c

fun String.ensureNotBlank(s: String) = if(isNotBlank()) this else s