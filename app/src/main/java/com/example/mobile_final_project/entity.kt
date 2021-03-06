package com.example.mobile_final_project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "point_table", indices = arrayOf(
    Index(value = ["altitude", "longtitude"],
        unique = true)
))
data class Cell(
    @PrimaryKey(autoGenerate = true)
    var ID: Long?=null,
    var cellId: Long?=null,
    var RSRP: String?,
    var RSRQ: String?,
    var CINR: String?,
    var AC: String?,
    var PLMN: String?,
    var altitude: Double,
    var longtitude: Double,
    var cellType: String?,
    var currentTime : String?,
    var latency : String?,
    var downloadRate : String?,
    var uploadRate : String?

)
@Entity(tableName = "rout_table")
data class Rout(
    @PrimaryKey(autoGenerate = true)
    var ID: Long ?= null,
    var rout: String?
)