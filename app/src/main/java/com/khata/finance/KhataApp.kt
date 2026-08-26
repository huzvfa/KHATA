package com.khata.finance

import android.app.Application
import com.khata.finance.data.AppDatabase
import com.khata.finance.repository.FinanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KhataApp : Application() {
    lateinit var repository: FinanceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = FinanceRepository.getInstance(db)
        CoroutineScope(Dispatchers.IO).launch {
            repository.seedDefaultCategories()
        }
    }
}
