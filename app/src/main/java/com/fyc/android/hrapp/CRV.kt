package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView

class CRV(val clickListener: onClickListener, val daysOfMonth: ArrayList<String>, val day: Int, val monthYear: String, val mYTV: TextView, val hl: ArrayList<Holidays>): RecyclerView.Adapter<CRV.CRVViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CRV.CRVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent,false)

        return CRVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CRVViewHolder, position: Int) {

        val currentWorker = daysOfMonth[position]

        holder.day.text = currentWorker

//        val fhl = hl.filter { it.day.contains(mYTV.text.toString()) }

        for(h in hl) {
            if (h.day == "$currentWorker " + mYTV.text.toString()) {
                holder.h.text = "Holiday"
            }
        }

//        do {
//            holder.h.text = "Holiday"
//        } while (hl.contains(Holidays("$currentWorker " + mYTV.text.toString())))

        if (currentWorker == day.toString() && mYTV.text == monthYear) {
            holder.calendarCell.setBackgroundResource(R.drawable.rec_shape_white)
            holder.day.setTextColor(Color.BLACK)
            holder.h.setTextColor(Color.BLACK)
        }

//        if (hl.contains(Holidays("$currentWorker " + mYTV.text.toString()))) {
//            holder.h.text = "Holiday"
//        }


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

            val h: TextView = itemView.findViewById(R.id.hName)

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