package com.alexeymerov.unsplashviewer.presentation.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.data.entity.ImageEntity
import com.alexeymerov.unsplashviewer.domain.ImageViewModel
import com.alexeymerov.unsplashviewer.presentation.adapter.ImageRecyclerAdapter
import com.alexeymerov.unsplashviewer.presentation.base.BaseActivity
import com.alexeymerov.unsplashviewer.utils.EndlessRecyclerViewScrollListener
import com.alexeymerov.unsplashviewer.utils.extensions.dpToPx
import com.alexeymerov.unsplashviewer.utils.extensions.getColorEx
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    private val viewModel by viewModel<ImageViewModel>()
    private val imageRecyclerAdapter by lazy { initRecyclerAdapter() }
    private val layoutManager by lazy { initLayoutManager() }
    private var isInSearch = false
    private lateinit var searchMenu: Menu
    private lateinit var menuItemSearch: MenuItem
    private lateinit var lastQuery: String
    private lateinit var searchDisposable: Disposable
    private lateinit var paginationListener: EndlessRecyclerViewScrollListener

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
        if (item.itemId == R.id.action_search) {
            menuItemSearch.expandActionView()
            circleReveal(searchToolbar, true)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        searchDisposable.dispose()
        super.onDestroy()
    }

    private fun initViews() {
        initToolbar(getString(R.string.toolbar_title))
        initSearchToolbar()
        initRecycler()
    }

    private fun initSearchToolbar() {
        searchToolbar.inflateMenu(R.menu.menu_search)
        searchToolbar.setNavigationOnClickListener { circleReveal(searchToolbar, false) }

        searchMenu = searchToolbar.menu
        menuItemSearch = searchMenu.findItem(R.id.action_filter_search)
        menuItemSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                circleReveal(searchToolbar, true)
                isInSearch = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                circleReveal(searchToolbar, false)
                isInSearch = false
                viewModel.loadImages()
                return true
            }
        })
        initSearchView()
    }

    private fun initSearchView() {
        val searchView = searchMenu.findItem(R.id.action_filter_search)?.actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.maxWidth = Integer.MAX_VALUE

        val closeButton = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_close_black)

        val txtSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Search..."
        txtSearch.setHintTextColor(Color.DKGRAY)
        txtSearch.setTextColor(getColorEx(R.color.colorPrimary))

        searchDisposable = RxSearchView.queryTextChanges(searchView)
                .skipInitialValue()
                .filter { !it.isEmpty() }
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    lastQuery = it.toString()
                    paginationListener.resetState()
                    viewModel.searchImages(it.toString())
                }
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
                if (!isInSearch) viewModel.loadNext(page + 1) //api not allowed send page lower 1
                else if (::lastQuery.isInitialized) viewModel.searchImagesNext(lastQuery, page + 1)
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
        intent.putExtra(ImageActivity.IMAGE_ENTITY, image)
        val options = ActivityOptions.makeSceneTransitionAnimation(this, view, "image")
        startActivity(intent, options.toBundle())
    }

    private fun circleReveal(view: View, needShow: Boolean) = circleReveal(view.id, needShow)

    private fun circleReveal(viewId: Int, needShow: Boolean) {
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
}
