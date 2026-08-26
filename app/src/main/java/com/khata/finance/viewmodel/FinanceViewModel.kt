package com.khata.finance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.khata.finance.KhataApp
import com.khata.finance.data.Category
import com.khata.finance.data.SavingsGoal
import com.khata.finance.data.Transaction
import com.khata.finance.data.TxnSource
import com.khata.finance.data.TxnType
import com.khata.finance.data.UnparsedSms
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as KhataApp).repository

    val transactions: StateFlow<List<Transaction>> =
        repo.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> =
        repo.allCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<SavingsGoal>> =
        repo.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedSms: StateFlow<List<UnparsedSms>> =
        repo.unresolvedSms.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val netBalance: StateFlow<Double> =
        repo.netBalance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun monthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }

    fun monthlyIncome(): Flow<Double> {
        val (s, e) = monthRange()
        return repo.incomeBetween(s, e)
    }

    fun monthlyExpense(): Flow<Double> {
        val (s, e) = monthRange()
        return repo.expenseBetween(s, e)
    }

    fun addTransaction(amount: Double, type: TxnType, categoryId: Long?, note: String) {
        viewModelScope.launch {
            repo.addTransaction(
                Transaction(
                    amount = amount, type = type, categoryId = categoryId,
                    note = note, dateMillis = System.currentTimeMillis(), source = TxnSource.MANUAL
                )
            )
        }
    }

    fun deleteTransaction(t: Transaction) = viewModelScope.launch { repo.deleteTransaction(t) }

    fun addCategory(name: String, type: TxnType, budget: Double?) {
        viewModelScope.launch { repo.addCategory(Category(name = name, type = type, monthlyBudget = budget)) }
    }

    fun addGoal(name: String, target: Double) {
        viewModelScope.launch { repo.addGoal(SavingsGoal(name = name, targetAmount = target)) }
    }

    fun contributeToGoal(goal: SavingsGoal, amount: Double) {
        viewModelScope.launch { repo.updateGoal(goal.copy(currentAmount = goal.currentAmount + amount)) }
    }

    fun resolveSms(item: UnparsedSms, amount: Double, type: TxnType, categoryId: Long?, note: String) {
        viewModelScope.launch {
            repo.addTransaction(
                Transaction(
                    amount = amount, type = type, categoryId = categoryId, note = note,
                    dateMillis = item.receivedMillis, source = TxnSource.SMS, rawSms = item.body
                )
            )
            repo.resolveUnparsedSms(item)
        }
    }

    fun dismissSms(item: UnparsedSms) = viewModelScope.launch { repo.deleteUnparsedSms(item) }
}
