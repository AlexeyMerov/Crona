package com.alexeymerov.unsplashviewer.presentation.base

import android.app.ActionBar
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.alexeymerov.unsplashviewer.utils.hideKeyboardEx
import com.alexeymerov.unsplashviewer.utils.showSnack
import com.alexeymerov.unsplashviewer.utils.showToast

abstract class BaseFragment : Fragment() {

    private val appBar: ActionBar? = activity?.actionBar

    protected fun disableHomeAsUp() = appBar?.setDisplayHomeAsUpEnabled(false)

    protected fun initializeNavigationBar(title: String, showBackButton: Boolean, @DrawableRes resId: Int) {
        appBar?.apply {
            setTitle(title)
            setDisplayHomeAsUpEnabled(showBackButton)
            setHomeAsUpIndicator(resId)
            elevation = 4f
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> fragmentManager?.popBackStackImmediate()
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(text: String) = activity?.showToast(text)

    protected fun showSnack(text: String) = activity?.showSnack(text)

    protected fun hideKeyboard() = activity?.hideKeyboardEx()
}
