package com.czm.xcricheditor.editor

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State?) {

        if (parent.getChildAdapterPosition(view) != 0)
            outRect.top = space
    }
}
