package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class AdminRV(val clickListener: onClickListener, val adminList: ArrayList<Admin>): RecyclerView.Adapter<AdminRV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.admin_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        val currentWorker = adminList[position]

        holder.name.text = currentWorker.email

        holder.type.text = currentWorker.type

    }

    override fun getItemCount(): Int {
        return adminList.size
    }

    interface onClickListener {
        fun onItemClick(position: Int)
    }

    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        val name : TextView = itemView.findViewById(R.id.admin_name)

        val type : TextView = itemView.findViewById(R.id.admin_type)

        val adminItem : ConstraintLayout = itemView.findViewById(R.id.admin_item)

        init {
            adminItem.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}