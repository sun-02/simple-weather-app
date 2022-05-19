package com.example.simpleweatherapp.util

import android.view.ViewGroup
import androidx.core.view.marginTop
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

fun MaterialToolbar.setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(statusBatHeight: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams)
        .topMargin = (parent as AppBarLayout).marginTop + statusBatHeight
}