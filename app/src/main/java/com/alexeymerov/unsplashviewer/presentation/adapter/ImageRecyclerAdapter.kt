package com.alexeymerov.unsplashviewer.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.alexeymerov.unsplashviewer.presentation.adapter.ImageRecyclerAdapter.ViewHolder
import com.alexeymerov.unsplashviewer.utils.GlideApp
import com.alexeymerov.unsplashviewer.utils.GlideRequests
import com.alexeymerov.unsplashviewer.utils.inflate
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_image.view.*


class ImageRecyclerAdapter() : BaseRecyclerAdapter<ImageEntity, ViewHolder>() {

    private lateinit var glideRequests: GlideRequests
    private lateinit var onItemClicked: (ImageEntity, View) -> Unit

    constructor(context: Context, onImageClicked: (ImageEntity, View) -> Unit) : this() {
        glideRequests = GlideApp.with(context)
        onItemClicked = onImageClicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ImageViewHolder(parent.inflate(R.layout.item_image))

    override fun getItemViewType(position: Int) = 0

    override fun compareItems(old: ImageEntity, new: ImageEntity) = old.id == new.id

    override fun compareContent(old: ImageEntity, new: ImageEntity) = LinkedHashSet<ImageEntity>()

    override fun proceedPayloads(payloads: MutableList<Any>, holder: ViewHolder, position: Int) {
        // handle changes from payloads
        holder.bind(items.elementAt(position))
    }

    abstract inner class ViewHolder(override val containerView: View?) : BaseViewHolder<ImageEntity>(containerView) {
        override fun bind(currentItem: ImageEntity) {
            // do something base things
        }
    }

    inner class ImageViewHolder(containerView: View?) : ViewHolder(containerView) {
        override fun bind(currentItem: ImageEntity) {
            super.bind(currentItem)
            containerView?.apply {
                glideRequests.setDefaultRequestOptions(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .load(currentItem.urls.thumb)
                        .dontTransform()
                        .transition(withCrossFade())
                        .placeholder(ColorDrawable(Color.parseColor(currentItem.color)))
                        .error(ColorDrawable(Color.parseColor(currentItem.color)))
                        .into(imageItem)

                imageItem.setOnClickListener { onItemClicked.invoke(items.elementAt(adapterPosition), this) }
            }
        }
    }

}
