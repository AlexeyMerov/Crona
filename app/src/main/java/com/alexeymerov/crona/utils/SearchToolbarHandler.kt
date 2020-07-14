package com.alexeymerov.crona.utils

import android.app.Activity
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.alexeymerov.crona.R
import com.alexeymerov.crona.utils.extensions.circleReveal
import com.alexeymerov.crona.utils.extensions.getColorEx
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SearchToolbarHandler(val searchToolbar: Toolbar, val activity: Activity, val onCollapse: () -> Unit, val onNextSearch: (String) -> Unit) {

    var isInSearch = false
    lateinit var lastQuery: String
    private lateinit var searchMenu: Menu
    private lateinit var menuItemSearch: MenuItem
    private lateinit var searchDisposable: Disposable

    init {
        initSearchToolbar()
    }

    fun onOptionsItemSelected(item: MenuItem) {
        if (item.itemId == R.id.action_search) {
            menuItemSearch.expandActionView()
            activity.circleReveal(searchToolbar, true)
        }
    }

    fun onDestroy() = searchDisposable.dispose()

    private fun initSearchToolbar() {
        searchToolbar.inflateMenu(R.menu.menu_search)
        searchToolbar.setNavigationOnClickListener { activity.circleReveal(searchToolbar, false) }

        searchMenu = searchToolbar.menu
        menuItemSearch = searchMenu.findItem(R.id.action_filter_search)
        menuItemSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                activity.circleReveal(searchToolbar, true)
                isInSearch = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                activity.circleReveal(searchToolbar, false)
                isInSearch = false
                onCollapse.invoke()
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
        txtSearch.setTextColor(activity.getColorEx(R.color.colorPrimary))

        searchDisposable = RxSearchView.queryTextChanges(searchView)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .map { it.toString() }
            .subscribe {
                lastQuery = it
                onNextSearch(it)
            }
    }

}