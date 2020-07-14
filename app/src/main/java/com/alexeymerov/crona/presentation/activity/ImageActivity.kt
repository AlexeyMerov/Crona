package com.alexeymerov.crona.presentation.activity

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
import com.alexeymerov.crona.R
import com.alexeymerov.crona.presentation.base.BaseActivity
import com.alexeymerov.crona.utils.CustomImageViewTarget
import com.alexeymerov.crona.utils.GlideApp
import com.alexeymerov.crona.utils.SilentTransitionListener
import com.alexeymerov.crona.utils.extensions.currentMillis
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File


class ImageActivity : BaseActivity() {

    companion object {
        const val IMAGE_BUNDLE = "image_bundle"

        const val IMAGE_THUMB = "image_thumb"
        const val IMAGE_REGULAR = "image_regular"
        const val IMAGE_COLOR = "image_color"

        private const val TYPE_IMAGE_JPEG = "image/jpeg"
        private const val IMAGE_PREFIX = "IMG_"
        private const val IMAGE_EXTENSION_JPG = ".jpg"
        private const val SEPARATOR = "/"
        private const val APP_PROVIDER_PATH = "com.alexeymerov.crona.provider"

        private const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private lateinit var urlThumb: String
    private lateinit var urlRegular: String
    private lateinit var color: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initViews()
        parseArguments()
        prepareImage()
    }

    private fun initViews() {
        fullImage.setOnClickListener { toggleToolbarVisibility() }
    }

    private fun parseArguments() {
        intent.getBundleExtra(IMAGE_BUNDLE)?.apply {
            urlThumb = getString(IMAGE_THUMB)!!
            urlRegular = getString(IMAGE_REGULAR)!!
            color = getString(IMAGE_COLOR)!!
        }
    }

    private fun prepareImage() {
        postponeEnterTransition()

        GlideApp.with(this)
            .load(urlThumb)
            .onlyRetrieveFromCache(true)
            .error(ColorDrawable(Color.parseColor(color)))
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
                .load(urlRegular)
                .placeholder(fullImage.drawable)
                .into(object : CustomImageViewTarget<Drawable>(fullImage) {
                    override fun onResourceReady(resource: Drawable) {
                        fullImage.setImageDrawable(resource)
                        initToolbar(iconRes = R.drawable.ic_arrow_back_white, enableHomeButton = true)
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finishAfterTransition()
            R.id.shareImage -> shareImage()
            R.id.saveImage -> saveImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareImage() {
        GlideApp.with(this)
            .asFile()
            .load(urlRegular)
            .onlyRetrieveFromCache(true)
            .into(object : CustomImageViewTarget<File>(fullImage) {
                override fun onResourceReady(resource: File) {
                    shareImage(resource)
                }
            })
    }

    private fun shareImage(resource: File) {
        val authority = APP_PROVIDER_PATH
        val uriForFile = FileProvider.getUriForFile(this, authority, resource)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = TYPE_IMAGE_JPEG
            putExtra(Intent.EXTRA_STREAM, uriForFile)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
    }

    private fun saveImage() {
        checkPermissions {
            createFolderIfNeeded()

            val request = DownloadManager.Request(Uri.parse(urlRegular)).apply {
                setTitle(getString(R.string.download_title))
                setMimeType(TYPE_IMAGE_JPEG)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                val fileName = IMAGE_PREFIX + currentMillis() + IMAGE_EXTENSION_JPG
                val subPath = getString(R.string.app_name) + File.separator + fileName
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, subPath)
            }

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    private fun createFolderIfNeeded() {
        val pictureDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val pathName = pictureDirectory?.absolutePath + SEPARATOR + getString(R.string.app_name)
        val finalFolder = File(pathName)
        if (!finalFolder.exists()) finalFolder.mkdir()
    }

    private inline fun checkPermissions(crossinline f: () -> Unit) {
        RxPermissions(this).apply {
            when {
                isGranted(WRITE_PERMISSION) && isGranted(READ_PERMISSION) -> f.invoke()
                else -> request(WRITE_PERMISSION, READ_PERMISSION)
                    .subscribe { isGranted -> if (isGranted) f.invoke() }
            }
        }
    }
}

