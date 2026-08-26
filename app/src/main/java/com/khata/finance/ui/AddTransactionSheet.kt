package com.khata.finance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.khata.finance.data.TxnType
import com.khata.finance.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(viewModel: FinanceViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.categories.collectAsState()
    var type by remember { mutableStateOf(TxnType.EXPENSE) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp).fillMaxWidth()) {
            Text("Add Transaction", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Row {
                FilterChip(selected = type == TxnType.EXPENSE, onClick = { type = TxnType.EXPENSE; selectedCategoryId = null }, label = { Text("Expense") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = type == TxnType.INCOME, onClick = { type = TxnType.INCOME; selectedCategoryId = null }, label = { Text("Income") })
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = amountText, onValueChange = { amountText = it },
                label = { Text("Amount (PKR)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Note (e.g. Grocery, Salary)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text("Category", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.filter { it.type == type }) { c ->
                    FilterChip(
                        selected = selectedCategoryId == c.id,
                        onClick = { selectedCategoryId = c.id },
                        label = { Text("${c.icon} ${c.name}") }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        viewModel.addTransaction(amount, type, selectedCategoryId, note)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
            Spacer(Modifier.height(12.dp))
        }
    }
}
