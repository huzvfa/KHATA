package com.khata.finance.repository

import com.khata.finance.data.AppDatabase
import com.khata.finance.data.Category
import com.khata.finance.data.SavingsGoal
import com.khata.finance.data.Transaction
import com.khata.finance.data.TxnType
import com.khata.finance.data.UnparsedSms
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val db: AppDatabase) {
    val allTransactions: Flow<List<Transaction>> = db.transactionDao().getAll()
    val allCategories: Flow<List<Category>> = db.categoryDao().getAll()
    val allGoals: Flow<List<SavingsGoal>> = db.goalDao().getAll()
    val unresolvedSms: Flow<List<UnparsedSms>> = db.unparsedSmsDao().getUnresolved()
    val netBalance: Flow<Double> = db.transactionDao().getNetBalance()

    suspend fun addTransaction(t: Transaction) = db.transactionDao().insert(t)
    suspend fun updateTransaction(t: Transaction) = db.transactionDao().update(t)
    suspend fun deleteTransaction(t: Transaction) = db.transactionDao().delete(t)

    fun incomeBetween(start: Long, end: Long) = db.transactionDao().getTotalIncome(start, end)
    fun expenseBetween(start: Long, end: Long) = db.transactionDao().getTotalExpense(start, end)

    suspend fun addCategory(c: Category) = db.categoryDao().insert(c)
    suspend fun updateCategory(c: Category) = db.categoryDao().update(c)
    suspend fun deleteCategory(c: Category) = db.categoryDao().delete(c)
    suspend fun categoryCount() = db.categoryDao().count()

    suspend fun seedDefaultCategories() {
        if (categoryCount() > 0) return
        val defaults = listOf(
            Category(name = "Salary", type = TxnType.INCOME, icon = "💼"),
            Category(name = "Business", type = TxnType.INCOME, icon = "🏪"),
            Category(name = "Other Income", type = TxnType.INCOME, icon = "➕"),
            Category(name = "Food & Groceries", type = TxnType.EXPENSE, icon = "🍽️"),
            Category(name = "Transport", type = TxnType.EXPENSE, icon = "🚗"),
            Category(name = "Bills & Utilities", type = TxnType.EXPENSE, icon = "🧾"),
            Category(name = "Rent", type = TxnType.EXPENSE, icon = "🏠"),
            Category(name = "Shopping", type = TxnType.EXPENSE, icon = "🛍️"),
            Category(name = "Health", type = TxnType.EXPENSE, icon = "💊"),
            Category(name = "Entertainment", type = TxnType.EXPENSE, icon = "🎬"),
            Category(name = "Mobile & Internet", type = TxnType.EXPENSE, icon = "📱"),
            Category(name = "Other", type = TxnType.EXPENSE, icon = "🔹")
        )
        db.categoryDao().insertAll(defaults)
    }

    suspend fun addGoal(g: SavingsGoal) = db.goalDao().insert(g)
    suspend fun updateGoal(g: SavingsGoal) = db.goalDao().update(g)
    suspend fun deleteGoal(g: SavingsGoal) = db.goalDao().delete(g)

    suspend fun addUnparsedSms(item: UnparsedSms) = db.unparsedSmsDao().insert(item)
    suspend fun resolveUnparsedSms(item: UnparsedSms) = db.unparsedSmsDao().update(item.copy(resolved = true))
    suspend fun deleteUnparsedSms(item: UnparsedSms) = db.unparsedSmsDao().delete(item)

    companion object {
        @Volatile private var INSTANCE: FinanceRepository? = null
        fun getInstance(db: AppDatabase): FinanceRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FinanceRepository(db).also { INSTANCE = it }
            }
    }
}
