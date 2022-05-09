package com.example.simpleweatherapp.util

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