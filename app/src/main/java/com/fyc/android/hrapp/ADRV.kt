package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ADRV(val eList: List<Worker>): RecyclerView.Adapter<ADRV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.w_a_days_item, parent,false)

        return RVViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        val currentWorker = eList[position]

        holder.day.text = currentWorker.day

        holder.arrivalTime.text = "Arrival Time: " + currentWorker.aTime + ":" + currentWorker.aTimemin

        holder.leaveTime.text = "Leave Time: " + currentWorker.lTime + ":" + currentWorker.lTimemin
    }

    override fun getItemCount(): Int {
        return eList.size
    }


    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        val day : TextView = itemView.findViewById(R.id.day)

        val arrivalTime : TextView = itemView.findViewById(R.id.e_arrival_time)

        val leaveTime : TextView = itemView.findViewById(R.id.e_leave_time)

        val dayItem : CardView = itemView.findViewById(R.id.w_a_days_item)

    }
}