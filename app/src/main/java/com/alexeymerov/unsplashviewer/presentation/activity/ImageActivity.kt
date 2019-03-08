package com.alexeymerov.unsplashviewer.presentation.activity

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.data.entity.ImageEntity
import com.alexeymerov.unsplashviewer.presentation.base.BaseActivity
import com.alexeymerov.unsplashviewer.utils.CustomImageViewTarget
import com.alexeymerov.unsplashviewer.utils.GlideApp
import com.alexeymerov.unsplashviewer.utils.SilentTransitionListener
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File


class ImageActivity : BaseActivity() {

    companion object {
        const val IMAGE_ENTITY = "image_entity"
    }

    private lateinit var imageEntity: ImageEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initViews()
        parseArguments()
        prepareImage()
    }

    private fun initViews() {
        initToolbar(enableHomeButton = true, iconRes = R.drawable.ic_arrow_back_white)
        fullImage.setOnClickListener { toggleToolbarVisibility() }
    }

    private fun parseArguments() {
        imageEntity = intent.getParcelableExtra(IMAGE_ENTITY)
    }

    private fun prepareImage() {
        postponeEnterTransition()

        GlideApp.with(this)
                .load(imageEntity.urls.thumb)
                .onlyRetrieveFromCache(true)
                .error(ColorDrawable(Color.parseColor(imageEntity.color)))
                .into(object : CustomImageViewTarget<Drawable>(fullImage) {
                    override fun onResourceReady(resource: Drawable) {
                        fullImage.setImageDrawable(resource)
                        startPostponedEnterTransition()
                    }
                })

        window.sharedElementEnterTransition.addListener(object : SilentTransitionListener() {
            override fun onTransitionEnd() {
                loadFullImage()
            }
        })
    }

    private fun loadFullImage() {
        fullImage.post {
            GlideApp.with(this)
                    .load(imageEntity.urls.regular)
                    .placeholder(fullImage.drawable)
                    .into(fullImage)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.home -> finishAfterTransition()
            R.id.shareImage -> shareImage()
            R.id.saveImage -> saveImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareImage() {
        GlideApp.with(this)
                .asFile()
                .load(imageEntity.urls.regular)
                .onlyRetrieveFromCache(true)
                .into(object : CustomImageViewTarget<File>(fullImage) {
                    override fun onResourceReady(resource: File) {
                        shareImage(resource)
                    }
                })
    }

    private fun shareImage(resource: File) {
        val authority = "com.alexeymerov.unsplashviewer.provider"
        val uriForFile = FileProvider.getUriForFile(this, authority, resource)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uriForFile)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
    }

    private fun saveImage() {
        checkPermissions {
            val request = DownloadManager.Request(Uri.parse(imageEntity.urls.raw)).apply {
                setTitle(getString(R.string.download_title))
                allowScanningByMediaScanner()
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, getString(R.string.app_name))
            }

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    private inline fun checkPermissions(crossinline f: () -> Unit) {
        RxPermissions(this).apply {
            when {
                isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) -> f.invoke()
                else -> request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe { isGranted -> if (isGranted) f.invoke() }
            }
        }
    }
}

