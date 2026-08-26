package com.khata.finance.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khata.finance.data.TxnType
import com.khata.finance.util.CurrencyUtils
import com.khata.finance.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        if (transactions.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions yet")
                }
            }
        }
        items(transactions, key = { it.id }) { t ->
            val cat = categories.find { it.id == t.categoryId }
            ListItem(
                headlineContent = { Text(t.note.ifBlank { cat?.name ?: "Uncategorized" }) },
                supportingContent = {
                    Text("${cat?.name ?: "Uncategorized"} · ${sdf.format(Date(t.dateMillis))}${if (t.source.name == "SMS") " · via SMS" else ""}")
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            (if (t.type == TxnType.INCOME) "+" else "-") + CurrencyUtils.format(t.amount),
                            color = if (t.type == TxnType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = { viewModel.deleteTransaction(t) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
            HorizontalDivider()
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
