package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.temporal.TemporalAmount

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val label:String,
    val amount:Double,
    val description:String):Serializable{
}
