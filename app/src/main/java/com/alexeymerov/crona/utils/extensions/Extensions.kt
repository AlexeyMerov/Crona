package com.alexeymerov.crona.utils.extensions

import android.content.res.Resources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*


interface AutoUpdatableAdapterSet<T> {
    fun RecyclerView.Adapter<*>.autoNotifySet(oldSet: LinkedHashSet<T>, newSet: LinkedHashSet<T>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int) =
                compareItems(oldSet.elementAt(oldPosition), newSet.elementAt(newPosition))

            override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = oldSet.elementAt(oldPosition) == newSet.elementAt(newPosition)
            override fun getOldListSize() = oldSet.size
            override fun getNewListSize() = newSet.size
            override fun getChangePayload(oldPosition: Int, newPosition: Int) =
                compareContent(oldSet.elementAt(oldPosition), newSet.elementAt(newPosition))

        }).dispatchUpdatesTo(this)
    }

    fun compareItems(old: T, new: T): Boolean

    fun compareContent(old: T, new: T): LinkedHashSet<T>? = null
}

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun currentMillis() = System.currentTimeMillis()