package com.khata.finance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khata.finance.data.Category
import com.khata.finance.data.TxnType
import com.khata.finance.util.CurrencyUtils
import com.khata.finance.viewmodel.FinanceViewModel

@Composable
fun BudgetsScreen(viewModel: FinanceViewModel) {
    val categories by viewModel.categories.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val (start, end) = viewModel.monthRange()
    var showAddCategory by remember { mutableStateOf(false) }

    val expenseCategories = categories.filter { it.type == TxnType.EXPENSE }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Monthly Budgets", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { showAddCategory = true }) { Text("+ Category") }
            }
        }
        items(expenseCategories) { c ->
            val spent = transactions.filter { it.categoryId == c.id && it.dateMillis in start..end }.sumOf { it.amount }
            BudgetRow(c, spent)
        }
    }

    if (showAddCategory) {
        var name by remember { mutableStateOf("") }
        var budget by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategory = false },
            title = { Text("New Category") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text("Monthly budget (optional, PKR)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addCategory(name, TxnType.EXPENSE, budget.toDoubleOrNull())
                        showAddCategory = false
                    }
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddCategory = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun BudgetRow(category: Category, spent: Double) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${category.icon} ${category.name}", style = MaterialTheme.typography.titleMedium)
                Text(CurrencyUtils.format(spent) + (category.monthlyBudget?.let { " / ${CurrencyUtils.format(it)}" } ?: ""))
            }
            val budget = category.monthlyBudget
            if (budget != null && budget > 0) {
                Spacer(Modifier.height(8.dp))
                val progress = (spent / budget).toFloat().coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                if (spent > budget) {
                    Text(
                        "Over budget by ${CurrencyUtils.format(spent - budget)}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
