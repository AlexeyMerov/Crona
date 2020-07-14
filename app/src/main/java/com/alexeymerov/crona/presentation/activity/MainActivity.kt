package com.alexeymerov.crona.presentation.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.crona.R
import com.alexeymerov.crona.data.entity.ImageEntity
import com.alexeymerov.crona.domain.interfaces.IImageViewModel
import com.alexeymerov.crona.presentation.adapter.ImageRecyclerAdapter
import com.alexeymerov.crona.presentation.base.BaseActivity
import com.alexeymerov.crona.utils.EndlessRecyclerViewScrollListener
import com.alexeymerov.crona.utils.SearchToolbarHandler
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity() {

    private val viewModel by viewModel<IImageViewModel>()
    private val imageRecyclerAdapter by lazy { initRecyclerAdapter() }
    private val layoutManager by lazy { initLayoutManager() }

    private lateinit var paginationListener: EndlessRecyclerViewScrollListener
    private lateinit var searchToolbarHandler: SearchToolbarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        searchToolbarHandler.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        searchToolbarHandler.onDestroy()
        super.onDestroy()
    }

    private fun initViews() {
        initToolbar(getString(R.string.toolbar_title))
        initSearchToolbar()
        initRecycler()
    }

    private fun initSearchToolbar() {
        searchToolbarHandler = SearchToolbarHandler(searchToolbar, this,
            onCollapse = {
                paginationListener.resetState()
                viewModel.loadImages()
            },
            onNextSearch = {
                paginationListener.resetState()
                viewModel.searchImages(it)
            })
    }

    private fun initLayoutManager() = GridLayoutManager(this, 3)
        .apply {
            isMeasurementCacheEnabled = true
            isItemPrefetchEnabled = true
            orientation = RecyclerView.VERTICAL
        }

    private fun initRecyclerAdapter() = ImageRecyclerAdapter(this, ::onImageClick)

    private fun initRecycler() {
        paginationListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                progressBar.visibility = View.VISIBLE
                if (searchToolbarHandler.isInSearch) viewModel.searchImagesNext(searchToolbarHandler.lastQuery, page + 1)
                else viewModel.loadNext(page + 1) //api not allowed send page lower 1
            }
        }
        imageRecycler.also {
            it.setItemViewCacheSize(20)
//            it.isDrawingCacheEnabled = true
//            it.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            it.layoutManager = layoutManager
            it.adapter = imageRecyclerAdapter
            it.addOnScrollListener(paginationListener)
        }
    }

    private fun initObservers() {
        viewModel.notLocalImages.observe(this, Observer { newSet ->
            progressBar.visibility = View.GONE
            newSet?.apply { imageRecyclerAdapter.items = newSet }
        })
    }

    private fun onImageClick(image: ImageEntity, view: View) {
        val intent = Intent(this, ImageActivity::class.java)

        val data = Bundle().apply {
            putString(ImageActivity.IMAGE_THUMB, image.urls.thumb)
            putString(ImageActivity.IMAGE_REGULAR, image.urls.regular)
            putString(ImageActivity.IMAGE_COLOR, image.color)
        }

        intent.putExtra(ImageActivity.IMAGE_BUNDLE, data)

        val options = ActivityOptions.makeSceneTransitionAnimation(this, view, "image")
        startActivity(intent, options.toBundle())
    }

}