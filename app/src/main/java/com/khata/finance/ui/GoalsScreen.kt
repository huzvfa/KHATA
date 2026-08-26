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
import com.khata.finance.data.SavingsGoal
import com.khata.finance.util.CurrencyUtils
import com.khata.finance.viewmodel.FinanceViewModel

@Composable
fun GoalsScreen(viewModel: FinanceViewModel) {
    val goals by viewModel.goals.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var contributeTarget by remember { mutableStateOf<SavingsGoal?>(null) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Savings Goals", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { showAdd = true }) { Text("+ Goal") }
            }
        }
        items(goals) { g ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(g.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("${CurrencyUtils.format(g.currentAmount)} of ${CurrencyUtils.format(g.targetAmount)}")
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (g.currentAmount / g.targetAmount).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { contributeTarget = g }) { Text("Add contribution") }
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("New Goal") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Goal name (e.g. Emergency fund)") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target amount (PKR)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val t = target.toDoubleOrNull()
                    if (name.isNotBlank() && t != null && t > 0) {
                        viewModel.addGoal(name, t)
                        showAdd = false
                    }
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
        )
    }

    contributeTarget?.let { goal ->
        var amount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { contributeTarget = null },
            title = { Text("Add to ${goal.name}") },
            text = {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (PKR)") })
            },
            confirmButton = {
                TextButton(onClick = {
                    val a = amount.toDoubleOrNull()
                    if (a != null && a > 0) {
                        viewModel.contributeToGoal(goal, a)
                        contributeTarget = null
                    }
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { contributeTarget = null }) { Text("Cancel") } }
        )
    }
}
