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
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.alexeymerov.unsplashviewer.presentation.base.BaseActivity
import com.alexeymerov.unsplashviewer.utils.GlideApp
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File


const val IMAGE_ENTITY = "image_entity"

class ImageActivity : BaseActivity() {

    private lateinit var imageEntity: ImageEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initializeToolbar("", true, R.drawable.ic_arrow_back_white)
        prepareImage()
    }

    private fun prepareImage() {
        postponeEnterTransition()

        imageEntity = intent.getParcelableExtra(IMAGE_ENTITY)

        GlideApp.with(this)
                .load(imageEntity.urls.thumb)
                .onlyRetrieveFromCache(true)
                .error(ColorDrawable(Color.parseColor(imageEntity.color)))
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        fullImage.setImageDrawable(resource)
                        startPostponedEnterTransition()
                    }
                })
        window.sharedElementEnterTransition.addListener(object : android.transition.Transition.TransitionListener {
            override fun onTransitionResume(transition: android.transition.Transition?) {}

            override fun onTransitionPause(transition: android.transition.Transition?) {}

            override fun onTransitionCancel(transition: android.transition.Transition?) {}

            override fun onTransitionStart(transition: android.transition.Transition?) {}

            override fun onTransitionEnd(transition: android.transition.Transition?) {
                loadFullImage()
            }
        })

        fullImage.setOnClickListener { toggleToolbarVisibility() }
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
                .into(object : SimpleTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        val uriForFile = FileProvider.getUriForFile(this@ImageActivity,
                                "com.alexeymerov.unsplashviewer.provider",
                                resource)

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, uriForFile)
                        }

                        startActivity(Intent.createChooser(shareIntent, "Share Image"))
                    }
                })
    }

    private fun saveImage() {
        checkPermissions {
            val request = DownloadManager.Request(Uri.parse(imageEntity.urls.raw)).apply {
                setTitle("Downloading image")
                allowScanningByMediaScanner()
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, getString(R.string.app_name))
            }

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    private inline fun checkPermissions(crossinline f: () -> Unit) {
        val rxPermissions = RxPermissions(this)
        if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            f.invoke()
        } else rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ isGranted -> if (isGranted) f.invoke() })
    }

}

