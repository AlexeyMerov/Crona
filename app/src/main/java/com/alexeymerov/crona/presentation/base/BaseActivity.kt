package com.alexeymerov.crona.presentation.base

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alexeymerov.crona.R

abstract class BaseActivity : AppCompatActivity() {

    protected fun initToolbar(titleText: String? = null,
                              enableHomeButton: Boolean = false,
                              @DrawableRes iconRes: Int? = null) {
        findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener { onBackPressed() }
            supportActionBar?.apply {
                title = titleText
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

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }


}