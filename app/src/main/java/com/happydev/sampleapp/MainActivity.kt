package com.happydev.sampleapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.happydev.sampleapp.adapter.BusinessAdapter
import com.happydev.sampleapp.models.Business
import com.happydev.sampleapp.repository.BusinessRepository
import com.happydev.sampleapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.happydev.sampleapp.util.Resource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: BusinessViewModel
    private val businessAdapter = BusinessAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerview()

        swipeRefreshLayout.setOnRefreshListener {
            search(true)
        }

        btnSearch.setOnClickListener(this)
        fab.setOnClickListener(this)
        rootLayout.setOnClickListener(this)

        businessAdapter.setOnItemClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }

        val viewModelProviderFactory = BusinessViewModelProviderFactory(application as App)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(BusinessViewModel::class.java)

        viewModel.businesses.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { businessesResponse ->
                        businessesResponse.businesses?.let {
                            businessAdapter.updateList(it.toList())
                        }
                        businessesResponse.total?.let {
                            isLastPage = businessAdapter.currentList.size == it
                            if (isLastPage)
                                rvBusinesses.setPadding(0, 0, 0, 0)

                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { msg ->
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun setupRecyclerview() {
        rvBusinesses.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = businessAdapter
            addOnScrollListener(scrollListener)
        }
    }

    private fun search(isRefresh : Boolean) {
        val term = etSearch.text.toString().trim()
        val address = etAddress.text.toString().trim()
        when {
            term.isEmpty() -> Snackbar.make(rootLayout!!, "Please enter business type", Snackbar.LENGTH_SHORT).show()
            address.isEmpty() -> Snackbar.make(rootLayout!!, "Please enter address", Snackbar.LENGTH_SHORT).show()
            else -> viewModel.searchBusinesses(term, address, isRefresh)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            if(!isLoading && !isLastPage) {
                if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition > 0) {
                    isLoading = true
                    search(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btnSearch -> {
                search(false)
            }
            R.id.fab -> {
                businessAdapter.sortList()
            }
        }
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }


}