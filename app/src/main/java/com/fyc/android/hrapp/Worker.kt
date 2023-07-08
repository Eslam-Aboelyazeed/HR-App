package com.fyc.android.hrapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Worker(
                  var fName: String = "",
                  var lName: String = "",
                  var dOB: String = "",
                  var salary: String = "",
                  var pNumber: String = "",
                  var country: String = "",
                  var city: String = "",
                  var gender: String = "",
                  var nationality: String = "",
                  var nationalId:String = "",
                  var hireDate: String = "",
                  var aTime: Int = 0,
                  var lTime: Int = 0,
                  var department: String = "",
                  var day: String = "",
                  var dSalary: String = "",
                  var mSalary: String = ""
): Parcelable

@Parcelize
data class MonthSalary(
                       var fName: String = "",
                       var lName: String = "",
                       var dOB: String = "",
                       var month: String = "",
                       var salary: String = "",
                       var daysoff: String = "",
                       var bonus: String = "",
                       var deduction: String = ""
): Parcelable

@Parcelize
data class AttendedWorker(
                            var fName: String = "",
                            var lName: String = "",
                            var dOB: String = "",
                            var salary: String = "",
                            var pNumber: String = "",
                            var country: String = "",
                            var city: String = "",
                            var gender: String = "",
                            var nationality: String = "",
                            var nationalId:String = "",
                            var hireDate: String = "",
                            var aTime: Int = 0,
                            var lTime: Int = 0,
                            var department: String = "",
                            var day: String = "",
                            var dSalary: String = "",
                            var mSalary: String = ""
): Parcelable

@Parcelize
class Workers: ArrayList<Worker>(), Parcelable

@Parcelize
class AttendedWorkers: ArrayList<AttendedWorker>(), Parcelable

@Parcelize
data class WAttendance(
                        var day: String = "",
                        var workers: Worker= Worker()
): Parcelable

@Parcelize
data class Holidays(val day: String = ""): Parcelable