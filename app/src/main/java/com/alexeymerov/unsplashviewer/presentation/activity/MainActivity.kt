package com.alexeymerov.unsplashviewer.presentation.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.GridLayout.VERTICAL
import android.widget.ImageView
import com.alexeymerov.unsplashviewer.R
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.alexeymerov.unsplashviewer.domain.ImageViewModel
import com.alexeymerov.unsplashviewer.presentation.adapter.ImageRecyclerAdapter
import com.alexeymerov.unsplashviewer.presentation.base.BaseActivity
import com.alexeymerov.unsplashviewer.utils.EndlessRecyclerViewScrollListener
import com.alexeymerov.unsplashviewer.utils.dpToPx
import com.alexeymerov.unsplashviewer.utils.getColorEx
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    private val viewModel by viewModel<ImageViewModel>()
    private val imageRecyclerAdapter: ImageRecyclerAdapter by lazy { initRecyclerAdapter() }
    private val layoutManager by lazy { initLayoutManager() }
    private var searchMenu: Menu? = null
    private var menuItemSearch: MenuItem? = null
    private var lastQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeToolbar("Popular")
        setSearchTollbar()
        initRecycler()
    }

    private fun initRecycler() {
        imageRecycler.let {
            imageRecycler.setItemViewCacheSize(20)
            imageRecycler.isDrawingCacheEnabled = true
            imageRecycler.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            imageRecycler.layoutManager = layoutManager
            imageRecycler.adapter = imageRecyclerAdapter
            imageRecycler.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    progressBar.visibility = View.VISIBLE
                    if (!isInSearch) viewModel.loadNext(page + 1) //api not allowed send page lower 1
                    else if (lastQuery != null) viewModel.searchImagesNext(lastQuery!!, page + 1)
                }
            })
        }

        viewModel.notLocalImages.apply {
            value?.apply { imageRecyclerAdapter.items = this }
            observe(this@MainActivity, Observer {
                progressBar.visibility = View.GONE
                it?.apply { imageRecyclerAdapter.items = this }
            })
        }
    }

    private fun initLayoutManager() = GridLayoutManager(this, 3)
            .apply {
                isMeasurementCacheEnabled = true
                isItemPrefetchEnabled = true
                orientation = VERTICAL
            }

    private fun initRecyclerAdapter() = ImageRecyclerAdapter(this, this::onImageClick)

    private fun onImageClick(image: ImageEntity, view: View) {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra(IMAGE_ENTITY, image)
        val options = ActivityOptions.makeSceneTransitionAnimation(this, view, "image")
        startActivity(intent, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                circleReveal(searchToolbar, 1, true, true)
                menuItemSearch?.expandActionView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var isInSearch: Boolean = false

    private fun setSearchTollbar() {
        if (searchToolbar != null) {
            searchToolbar.inflateMenu(R.menu.menu_search)
            searchMenu = searchToolbar.menu

            searchToolbar.setNavigationOnClickListener({
                circleReveal(searchToolbar, 1, true, false)
            })

            menuItemSearch = searchMenu?.findItem(R.id.action_filter_search)

            menuItemSearch?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    circleReveal(searchToolbar, 1, true, false)
                    isInSearch = false
                    viewModel.loadImages()
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    isInSearch = true
                    return true
                }
            })
            initSearchView()
        }
    }

    private fun initSearchView() {
        val searchView = searchMenu?.findItem(R.id.action_filter_search)?.actionView as SearchView

        searchView.isSubmitButtonEnabled = false
        searchView.maxWidth = Integer.MAX_VALUE

        val closeButton = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_close_black)

        val txtSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Search..."
        txtSearch.setHintTextColor(Color.DKGRAY)
        txtSearch.setTextColor(getColorEx(R.color.colorPrimary))

        RxSearchView.queryTextChanges(searchView)
                .skipInitialValue()
                .filter { !it.isEmpty() }
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    lastQuery = it.toString()
                    viewModel.searchImages(it.toString())
                }
    }

    fun circleReveal(view: View, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        circleReveal(view.id, posFromRight, containsOverflow, isShow)
    }

    @SuppressLint("PrivateResource")
    private fun circleReveal(viewID: Int, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        val myView = findViewById<View>(viewID)
        val width = myView.width
        val cx = width - 28.dpToPx()
        val cy = myView.height / 2

        val anim = when {
            isShow -> ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, width.toFloat())
            else -> ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
        }

        anim.duration = 350.toLong()

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            }
        })

        if (isShow) myView.visibility = View.VISIBLE
        anim.start()
    }
}
