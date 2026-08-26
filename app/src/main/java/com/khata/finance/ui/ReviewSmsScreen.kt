package com.khata.finance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import com.khata.finance.data.TxnType
import com.khata.finance.data.UnparsedSms
import com.khata.finance.viewmodel.FinanceViewModel

@Composable
fun ReviewSmsScreen(viewModel: FinanceViewModel) {
    val items by viewModel.unresolvedSms.collectAsState()
    var editing by remember { mutableStateOf<UnparsedSms?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bank SMS to review", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "These looked like Meezan alerts but I couldn't confidently read the amount. Tap one to add it in a few seconds.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(12.dp))
        if (items.isEmpty()) {
            Text("Nothing to review \uD83C\uDF89")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(item.body, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            TextButton(onClick = { editing = item }) { Text("Add") }
                            TextButton(onClick = { viewModel.dismissSms(item) }) { Text("Ignore") }
                        }
                    }
                }
            }
        }
    }

    editing?.let { item ->
        var amount by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(TxnType.EXPENSE) }
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Add transaction") },
            text = {
                Column {
                    Row {
                        FilterChip(selected = type == TxnType.EXPENSE, onClick = { type = TxnType.EXPENSE }, label = { Text("Expense") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = type == TxnType.INCOME, onClick = { type = TxnType.INCOME }, label = { Text("Income") })
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (PKR)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val a = amount.toDoubleOrNull()
                    if (a != null && a > 0) {
                        viewModel.resolveSms(item, a, type, null, "From SMS")
                        editing = null
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancel") } }
        )
    }
}
