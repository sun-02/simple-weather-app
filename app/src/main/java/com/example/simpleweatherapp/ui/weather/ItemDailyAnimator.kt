package com.example.simpleweatherapp.ui.weather

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ItemDailyAnimator : DefaultItemAnimator(){

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ) = true

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) = true
}