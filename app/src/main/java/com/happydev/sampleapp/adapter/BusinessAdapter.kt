package com.happydev.sampleapp.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.happydev.sampleapp.R
import com.happydev.sampleapp.models.Business
import com.happydev.sampleapp.util.Constants
import kotlinx.android.synthetic.main.item_business.view.*
import java.util.*
import kotlin.collections.ArrayList

class BusinessAdapter : RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder>() {

    inner class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var currentList = ArrayList<Business>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        return BusinessViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_business,
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = currentList[position]
        holder.itemView.apply {
            business.image_url?.let {
                Glide.with(this).load(it).placeholder(R.drawable.shop).into(ivBusiness)
            }
            business.name.let {
                tvBusinessName.text = it
            }
            business.location?.let { obj ->

                var fullAddress = ""
                obj.address1?.let {
                    fullAddress += it
                }
                obj.city?.let {
                    fullAddress += " " + it
                }
                tvBusinessLocation.text = fullAddress
            }
            setOnClickListener {
                onItemClickListener?.let { it(business) }
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun updateList(list : List<Business>) {
        currentList = list as ArrayList<Business>
        notifyDataSetChanged()
    }

    fun sortList() {
        Collections.sort(currentList, { v1, v2 ->
            val s1: String = v1.name!!
            val s2: String = v2.name!!
            s1.compareTo(s2, ignoreCase = true)
        })
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((Business) -> Unit)? = null

    fun setOnItemClickListener(listener: (Business) -> Unit) {
        onItemClickListener = listener
    }
}