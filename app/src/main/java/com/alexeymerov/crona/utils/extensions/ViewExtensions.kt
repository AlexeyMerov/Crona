package com.alexeymerov.crona.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup

fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

fun Activity.circleReveal(view: View, needShow: Boolean) = circleReveal(view.id, needShow)

fun Activity.circleReveal(viewId: Int, needShow: Boolean) {
    val myView = findViewById<View>(viewId)
    val width = myView.width
    val centerX = width - 28.dpToPx()
    val centerY = myView.height / 2
    val startRadius = if (needShow) 0f else width.toFloat()
    val endRadius = if (needShow) width.toFloat() else 0f
    if (needShow) myView.visibility = View.VISIBLE

    val anim = ViewAnimationUtils.createCircularReveal(myView, centerX, centerY, startRadius, endRadius)
    anim.apply {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!needShow) myView.visibility = View.INVISIBLE
            }
        })
        duration = 350L
        start()
    }
}