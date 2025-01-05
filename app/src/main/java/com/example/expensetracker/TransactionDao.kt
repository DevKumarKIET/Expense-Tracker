package com.example.expensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * from transactions")
    fun getAll():List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

}