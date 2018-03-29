package com.alexeymerov.unsplashviewer.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.alexeymerov.unsplashviewer.utils.AutoUpdatableAdapterSet
import kotlinx.android.extensions.LayoutContainer
import kotlin.properties.Delegates

abstract class BaseRecyclerAdapter<T : Any, VH : BaseViewHolder<T>> : RecyclerView.Adapter<VH>(), AutoUpdatableAdapterSet<T> {

    var items: LinkedHashSet<T> by Delegates.observable(LinkedHashSet()) { _, oldSet, newSet -> autoNotifySet(oldSet, newSet) }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items.elementAt(position))

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) = when {
        payloads.isEmpty() -> onBindViewHolder(holder, position)
        else -> proceedPayloads(payloads, holder, position)
    }

    abstract fun proceedPayloads(payloads: MutableList<Any>, holder: VH, position: Int)
}

abstract class BaseViewHolder<in T : Any>(override val containerView: View?)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {
    abstract fun bind(currentItem: T)
}
