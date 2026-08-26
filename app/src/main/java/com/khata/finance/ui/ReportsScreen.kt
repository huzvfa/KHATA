package com.khata.finance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khata.finance.data.TxnType
import com.khata.finance.util.CurrencyUtils
import com.khata.finance.viewmodel.FinanceViewModel
import kotlin.math.max

@Composable
fun ReportsScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val (start, end) = viewModel.monthRange()

    val monthTx = transactions.filter { it.dateMillis in start..end }
    val expenseByCategory = monthTx.filter { it.type == TxnType.EXPENSE }
        .groupBy { it.categoryId }
        .map { (catId, list) -> (categories.find { it.id == catId }?.name ?: "Uncategorized") to list.sumOf { it.amount } }
        .sortedByDescending { it.second }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("This Month's Spending by Category", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        if (expenseByCategory.isEmpty()) {
            Text("No expenses logged this month yet.")
        } else {
            val maxVal = expenseByCategory.maxOf { it.second }
            expenseByCategory.forEach { (name, amount) ->
                Column(Modifier.padding(vertical = 6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name)
                        Text(CurrencyUtils.format(amount))
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (amount / max(maxVal, 1.0)).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    )
                }
            }
        }
    }
}
