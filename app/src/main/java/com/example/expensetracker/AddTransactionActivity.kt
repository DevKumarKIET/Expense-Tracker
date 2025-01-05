package com.example.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.expensetracker.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private val binding : ActivityAddTransactionBinding by lazy {
        ActivityAddTransactionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.labelInput.addTextChangedListener {
            if (it!!.count()>0)
                binding.labelLayout.error = null
        }

        binding.amountInput.addTextChangedListener {
            if (it!!.count()>0)
                binding.amountLayout.error = null
        }

        binding.addTransactionBtn.setOnClickListener {
            val label = binding.labelInput.text.toString()
            val description = binding.descriptionInput.text.toString()
            val amount = binding.amountInput.text.toString().toDoubleOrNull()

            if (label.isEmpty()){
                binding.labelLayout.error = "Please enter a valid label"
            }
            if (amount==null)
                binding.amountLayout.error = "Please enter a valid amount"
            else{
                val transaction = Transaction(0,label,amount, description)
                insert(transaction)
            }
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }
    private fun insert(transaction: Transaction){
        val db : AppDatabase = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}