package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class WRV(val clickListener: onClickListener, val workerList: ArrayList<Worker>): RecyclerView.Adapter<WRV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.worker_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        val currentWorker = workerList[position]

        holder.name.text = currentWorker.fName + " " + currentWorker.lName

    }

    override fun getItemCount(): Int {
        return workerList.size
    }

    interface onClickListener {
        fun onItemClick(position: Int)
    }

    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        val name : TextView = itemView.findViewById(R.id.worker_name)

        val workerItem : CardView = itemView.findViewById(R.id.worker_item)

        init {
            workerItem.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}