package com.example.expensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.expensetracker.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var transactions: List<Transaction>
    private lateinit var oldtransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        binding.recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        val itemTouchHelperFactory= object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelperFactory)
        swipeHelper.attachToRecyclerView(binding.recyclerview)

        binding.addBtn.setOnClickListener{
            val intent = Intent(this,AddTransactionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchAll(){

        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            runOnUiThread{
                updateDashBoard()
                transactionAdapter.setData(transactions)
            }
        }

    }

    private fun updateDashBoard(){
        val totalAmount : Double = transactions.map{it.amount}.sum()
        val budgeAmount : Double = transactions.filter{it.amount>0}.map{it.amount}.sum()
        val expenseAmount : Double = totalAmount - budgeAmount

        binding.balanceText.text = "₹%.2f".format(totalAmount)
        binding.budget.text = "₹%.2f".format(budgeAmount)
        binding.expense.text = "₹%.2f".format(expenseAmount)
    }

    private fun showSnackBar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view,"Transaction Deleted!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this,R.color.red))
            .setTextColor(ContextCompat.getColor(this,R.color.white))
            .show()
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)
            transactions = oldtransactions
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashBoard()
            }
        }
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldtransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)
            transactions = transactions.filter {it.id!=transaction.id}
            runOnUiThread {
                updateDashBoard()
                transactionAdapter.setData(transactions)
                showSnackBar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }

}



