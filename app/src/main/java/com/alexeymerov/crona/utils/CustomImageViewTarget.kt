package com.alexeymerov.crona.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

open class CustomImageViewTarget<R>(view: ImageView) : CustomViewTarget<ImageView, R>(view) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
    }

    override fun onResourceCleared(placeholder: Drawable?) {
    }

    override fun onResourceReady(resource: R, transition: Transition<in R>?) {
        onResourceReady(resource)
    }

    open fun onResourceReady(resource: R) {
    }

}