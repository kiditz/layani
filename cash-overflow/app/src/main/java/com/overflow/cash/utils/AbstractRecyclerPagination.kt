package com.overflow.cash.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Created by kiditz on 12/12/17.
 */

abstract class AbstractRecyclerPagination(private val manager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract val isLoading: Boolean
    abstract val isLastPage: Boolean
    abstract val totalItemCount: Int


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = manager.childCount
        val totalItemCount = manager.itemCount
        val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= this.totalItemCount) {
                loadMoreItems()
            }
        }
    }

    abstract fun loadMoreItems()
}
