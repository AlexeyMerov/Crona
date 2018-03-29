package com.alexeymerov.unsplashviewer.presentation.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.utils.hideKeyboardEx

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun initializeToolbar(titlText: String? = null,
                                    enableHomeButton: Boolean = false,
                                    @DrawableRes iconRes: Int? = null) {
        findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener({ onBackPressed() })
            supportActionBar?.apply {
                title = titlText
                setDisplayHomeAsUpEnabled(enableHomeButton)
                iconRes?.let { setNavigationIcon(iconRes) }
            }
        }
    }

    protected fun toggleToolbarVisibility() {
        supportActionBar?.apply {
            if (isShowing) hide() else show()
        }
    }

    protected fun setSubtitle(text: CharSequence) {
        supportActionBar?.subtitle = text
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    protected fun setToolbarTitle(title: CharSequence) {
        supportActionBar?.title = title
    }

    private fun hideKeyboard() = this.hideKeyboardEx()
}