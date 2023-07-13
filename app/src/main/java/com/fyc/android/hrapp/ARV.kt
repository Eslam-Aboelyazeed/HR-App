package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ARV(val clickListener: onClickListener, val aWorkerList: ArrayList<Worker>, val wList: ArrayList<Worker>): RecyclerView.Adapter<ARV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.w_attendance_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

//        for (worker in workerList) {
//            aWorkerList.add(AttendedWorker(worker.fName,worker.lName,worker.dOB,worker.salary,
//                worker.pNumber,worker.country,worker.city,worker.gender,worker.nationality,
//                worker.nationalId,worker.hireDate,0,0,worker.department))
//        }

        val currentWorker = aWorkerList[position]

        holder.name.text = currentWorker.fName + " " + currentWorker.lName

        for (w in wList) {

            if (w.fName == currentWorker.fName && w.lName == currentWorker.lName &&
                w.dOB == currentWorker.dOB) {

                holder.arrivalTime.text = "Arrival Time: " + w.aTime

                holder.leaveTime.text = "Leave Time: " + w.lTime



            }
//            else {
//
//                holder.arrivalTime.text = "Arrival Time: " + currentWorker.aTime
//
//                holder.leaveTime.text = "Leave Time: " + currentWorker.lTime
//
//            }

        }



//        holder.arrivalTime.text = "Arrival Time: " + currentWorker.aTime
//
//        holder.leaveTime.text = "Leave Time: " + currentWorker.lTime


//        fun makeItFocusable() {
//            holder.attendTime.isFocusableInTouchMode = true
//            holder.leaveTime.isFocusableInTouchMode = true
//        }
//
//        fun makeItUnFocusable() {
//            holder.attendTime.isFocusable = false
//            holder.leaveTime.isFocusable = false
//        }



//        holder.name.setOnCheckedChangeListener { buttonView, isChecked ->
//
//            if (isChecked){
//
//                attendedWorkerList.add(currentWorker)
//
//            } else {
//
//                attendedWorkerList.remove(currentWorker)
//
//            }
//        }
    }

    override fun getItemCount(): Int {
        return aWorkerList.size
    }

    interface onClickListener {
        fun onItemClick(position: Int)
    }

    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{


        val name : TextView = itemView.findViewById(R.id.attended_w_name)

        val arrivalTime : TextView = itemView.findViewById(R.id.arrival_time)

        val leaveTime : TextView = itemView.findViewById(R.id.leave_time)

        val workerItem : CardView = itemView.findViewById(R.id.w_attendance_item)


        init {

            workerItem.setOnClickListener(this)

        }


        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}