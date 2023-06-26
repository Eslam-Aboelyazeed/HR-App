package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ARV(val clickListener: onClickListener, val workerList: ArrayList<Worker>, val attendedWorkerList: ArrayList<Worker>): RecyclerView.Adapter<ARV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.w_attendance_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        val currentWorker = workerList[position]

        holder.name.text = currentWorker.fName + " " + currentWorker.lName

        holder.name.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked){

                attendedWorkerList.add(currentWorker)

            } else {

                attendedWorkerList.remove(currentWorker)

            }
        }
    }

    override fun getItemCount(): Int {
        return workerList.size
    }

    interface onClickListener {
        fun onItemClick(position: Int)
    }

    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{


        val name : CheckBox = itemView.findViewById(R.id.attended_w_name)

        val workerItem : ConstraintLayout= itemView.findViewById(R.id.w_attendance_item)

        init {
            workerItem.setOnClickListener(this)

        }


        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}