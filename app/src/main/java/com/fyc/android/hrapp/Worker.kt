package com.fyc.android.hrapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Worker(
                  var fName: String = "",
                  var lName: String = "",
                  var dOB: String = "",
                  var salary: String = "",
                  var pNumber: String = ""
): Parcelable

@Parcelize
class Workers: ArrayList<Worker>(), Parcelable