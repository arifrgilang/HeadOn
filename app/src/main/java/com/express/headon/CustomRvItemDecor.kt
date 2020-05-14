package com.express.headon

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomRvItemDecor(
    private val spaceHeight: Int,
    private val type: String
) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when(type){
            "top" -> {
                with(outRect){
                    if(parent.getChildAdapterPosition(view) == 0){
                        top = spaceHeight
                    }
                    left = spaceHeight
                    right = spaceHeight
                    bottom = spaceHeight
                }
            }
            "left" -> {
                with(outRect){
                    if(parent.getChildAdapterPosition(view) == 0){
                        left = spaceHeight
                    }
                    top = spaceHeight
                    right = spaceHeight
                    bottom = spaceHeight
                }
            }
        }
    }
}