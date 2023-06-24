package com.fyc.android.hrapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CRV(val clickListener: onClickListener, val daysOfMonth: ArrayList<String>): RecyclerView.Adapter<CRV.CRVViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CRV.CRVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent,false)

        return CRVViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CRVViewHolder, position: Int) {

        val currentWorker = daysOfMonth[position]

        holder.day.text = currentWorker

    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }
    interface onClickListener {
        fun onDayClick(day: String, position: Int)
    }

    inner class CRVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

            val day: TextView = itemView.findViewById(R.id.cellDayText)

            val calendarCell: ConstraintLayout = itemView.findViewById(R.id.calendar_cell)

        init {
            calendarCell.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onDayClick(daysOfMonth[adapterPosition],adapterPosition)
        }


    }

}