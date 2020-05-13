package com.express.headon.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.express.headon.R
import com.express.headon.model.HeadObject
import kotlinx.android.synthetic.main.item_barang.view.*

class HomeRvAdapter(
    private val ctx: Context,
    private val list: MutableList<HeadObject>,
    private val callback: OnBarangClickListener
) : RecyclerView.Adapter<HomeRvAdapter.ViewHolder>(){
    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(ctx)
                .inflate(R.layout.item_barang, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position], position)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(headObject: HeadObject, position: Int){
            itemView.tvNamaBarang.text = headObject.name
            itemView.tvPrice.text = headObject.price
            Glide.with(ctx)
                .load(headObject.imgUrl)
                .into(itemView.ivBarang)

            itemView.cvItem.setOnClickListener {
                headObject.path?.let{
                    callback.onClick(it)
                }
            }
        }
    }

    interface OnBarangClickListener{
        fun onClick(objectPath: String)
    }
}