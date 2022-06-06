package com.example.simpleweatherapp.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.ArrayMap

fun <T : Comparable<T>, U> getResFromRange(
    rangeToResourceMap: ArrayMap<ClosedRange<T>, U>,
    value: T
): U? {
    for (k in rangeToResourceMap.keys) {
        if (k.contains(value)) {
            return rangeToResourceMap[k]
        }
    }
    return null
}

fun intToSignedString(value: Int): String {
    val sign = if (value > 0) "+" else ""
    return "${sign}${value}"
}

fun paintStartValue(text: String, signedValue: String, color: Int) =
    SpannableString(text).apply {
        setSpan(
            ForegroundColorSpan(color),
            0, signedValue.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

fun paintEndValue(text: String, signedValue: String, color: Int) =
    SpannableString(text).apply {
        setSpan(
            ForegroundColorSpan(color),
            text.length - signedValue.length - 1, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }