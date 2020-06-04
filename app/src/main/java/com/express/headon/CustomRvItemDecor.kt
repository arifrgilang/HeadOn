package com.express.headon

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomRvItemDecor(
    private val ctx: Context,
    private val spaceHeight: Int,
    private val type: String
) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val customMargin = dpToPx(ctx, spaceHeight)
        when(type){
            "top" -> {
                with(outRect){
                    if(parent.getChildAdapterPosition(view) == 0){
                        top = customMargin
                    }
                    left = customMargin
                    right = customMargin
                    bottom = customMargin
                }
            }
            "left" -> {
                with(outRect){
                    if(parent.getChildAdapterPosition(view) == 0){
                        left = customMargin
                    }
                    top = customMargin
                    right = customMargin
                    bottom = customMargin
                }
            }
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}