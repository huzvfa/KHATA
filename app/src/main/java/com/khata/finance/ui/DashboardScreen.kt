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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khata.finance.data.TxnType
import com.khata.finance.util.CurrencyUtils
import com.khata.finance.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val balance by viewModel.netBalance.collectAsState()
    val income by viewModel.monthlyIncome().collectAsState(initial = 0.0)
    val expense by viewModel.monthlyExpense().collectAsState(initial = 0.0)
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total Balance", style = MaterialTheme.typography.labelLarge)
                    Text(
                        CurrencyUtils.formatWithDecimals(balance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("This month's income", style = MaterialTheme.typography.bodySmall)
                            Text(
                                CurrencyUtils.format(income),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("This month's expense", style = MaterialTheme.typography.bodySmall)
                            Text(
                                CurrencyUtils.format(expense),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        item { Text("Recent transactions", style = MaterialTheme.typography.titleMedium) }
        if (transactions.isEmpty()) {
            item {
                Text(
                    "No transactions yet. Add one with the + button, or let a Meezan SMS add it for you.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        items(transactions.take(10)) { t ->
            val cat = categories.find { it.id == t.categoryId }
            ListItem(
                headlineContent = { Text(t.note.ifBlank { cat?.name ?: "Uncategorized" }) },
                supportingContent = {
                    Text(cat?.name ?: (if (t.source.name == "SMS") "Auto-detected · needs category" else "Uncategorized"))
                },
                trailingContent = {
                    Text(
                        (if (t.type == TxnType.INCOME) "+" else "-") + CurrencyUtils.format(t.amount),
                        color = if (t.type == TxnType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
