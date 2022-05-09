package com.example.simpleweatherapp.util

import android.view.View
import android.view.ViewGroup


fun ViewGroup.getAllViews(): List<View> {
    val views: MutableList<View> = ArrayList()
    for (i in 0 until this.childCount) {
        views.add(this.getChildAt(i))
    }
    return views
}