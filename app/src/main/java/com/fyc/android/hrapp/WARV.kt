package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class WARV(val aWorkerList: ArrayList<AttendedWorker>,val workerList: ArrayList<Worker> ): RecyclerView.Adapter<WARV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.edit_w_attendance_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        for (worker in workerList) {
            aWorkerList.add(AttendedWorker(worker.fName,worker.lName,worker.dOB,worker.salary,
            worker.pNumber,worker.country,worker.city,worker.gender,worker.nationality,
                worker.nationalId,worker.hireDate,0,0,worker.department))
        }

        val currentWorker = aWorkerList[position]

//        currentWorker.fName = workerList[position].fName
//        currentWorker.lName = workerList[position].lName
//        currentWorker.dOB = workerList[position].dOB
//        currentWorker.country= workerList[position].country
//        currentWorker.city = workerList[position].city
//        currentWorker.nationality = workerList[position].nationality
//        currentWorker.nationalId = workerList[position].nationalId
//        currentWorker.gender = workerList[position].gender
//        currentWorker.hireDate= workerList[position].hireDate
//        currentWorker.pNumber= workerList[position].pNumber
//        currentWorker.salary = workerList[position].salary
//        currentWorker.department = workerList[position].department

        holder.name.text = currentWorker.fName + " " + currentWorker.lName

        holder.arrivalTime.setText(currentWorker.aTime.toString())

        holder.leaveTime.setText(currentWorker.lTime.toString())

        currentWorker.aTime = holder.arrivalTime.text.toString().toInt()

        currentWorker.lTime = holder.leaveTime.text.toString().toInt()

    }

    override fun getItemCount(): Int {
        return workerList.size
    }


    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        val name : TextView = itemView.findViewById(R.id.attended_w_name)

        val arrivalTime : EditText = itemView.findViewById(R.id.w_arrival_time)

        val leaveTime : EditText = itemView.findViewById(R.id.w_leave_time)


    }
}