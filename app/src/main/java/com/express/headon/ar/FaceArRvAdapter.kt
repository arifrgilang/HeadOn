package com.express.headon.ar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.express.headon.R
import com.express.headon.model.HeadObject
import kotlinx.android.synthetic.main.item_barang.view.*
import kotlinx.android.synthetic.main.item_recommended.view.*

class FaceArRvAdapter(
    private val ctx: Context,
    private val list: MutableList<HeadObject>,
    private val callback: OnBarangClickListener
) : RecyclerView.Adapter<FaceArRvAdapter.ViewHolder>(){
    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(ctx)
                .inflate(R.layout.item_recommended, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position], position)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(headObject: HeadObject, position: Int){
            itemView.tvRecommended.text = headObject.name
            Glide.with(ctx)
                .load(headObject.imgUrl)
                .into(itemView.ivRecommended)

            itemView.cvRecommended.setOnClickListener {
                callback.onClick(position)
            }
        }
    }

    interface OnBarangClickListener{
        fun onClick(position: Int)
    }
}