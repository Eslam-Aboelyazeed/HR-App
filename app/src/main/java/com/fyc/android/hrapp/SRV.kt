package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class SRV(val clickListener: onClickListener, val workerList: ArrayList<Worker>, val monthSalaryList: ArrayList<MonthSalary>, val month: String): RecyclerView.Adapter<SRV.RVViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.w_salary_item, parent,false)

        return RVViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {

        val currentWorker = workerList[position]

        holder.name.text = currentWorker.fName + " " + currentWorker.lName

        for (ms in monthSalaryList) {
            if (ms.fName == currentWorker.fName && ms.lName == currentWorker.lName &&
                ms.dOB == currentWorker.dOB && ms.month == month) {
                holder.salary.text = "This Month Salary: " + ms.salary
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
        View.OnClickListener {


        val name : TextView = itemView.findViewById(R.id.worker_name)

        val salary : TextView = itemView.findViewById(R.id.worker_salary)

        val salaryItem : ConstraintLayout = itemView.findViewById(R.id.w_salary_item)

        init {
            salaryItem.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val adapterPosition = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}