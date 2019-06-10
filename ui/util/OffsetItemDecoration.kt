package com.vozdelpueblo.coac.ui.dashboard.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.graphics.Point
import android.view.WindowManager

class OffsetItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = (getScreenWidth() / 2) - view.layoutParams.width / 2
        if (parent.getChildAdapterPosition(view) == 0) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = 0
            setupOutRect(outRect, offset, true)
        } else if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = 0
            setupOutRect(outRect, offset, false)
        }
    }

    private fun setupOutRect(rect: Rect, offset: Int, start: Boolean) {
        if (start) {
            rect.left = offset
        } else {
            rect.right = offset
        }
    }

    private fun getScreenWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }
}