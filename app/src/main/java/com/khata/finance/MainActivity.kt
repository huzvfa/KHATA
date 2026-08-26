package com.khata.finance

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.khata.finance.ui.AddTransactionSheet
import com.khata.finance.ui.BudgetsScreen
import com.khata.finance.ui.DashboardScreen
import com.khata.finance.ui.GoalsScreen
import com.khata.finance.ui.ReportsScreen
import com.khata.finance.ui.ReviewSmsScreen
import com.khata.finance.ui.TransactionsScreen
import com.khata.finance.ui.theme.KhataTheme
import com.khata.finance.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhataTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KhataAppRoot()
                }
            }
        }
    }
}

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    NavItem("dashboard", "Home", Icons.Filled.Home),
    NavItem("transactions", "History", Icons.Filled.List),
    NavItem("budgets", "Budgets", Icons.Filled.PieChart),
    NavItem("goals", "Goals", Icons.Filled.Savings),
    NavItem("reports", "Reports", Icons.Filled.BarChart)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataAppRoot() {
    val navController = rememberNavController()
    val viewModel: FinanceViewModel = viewModel()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val perms = mutableListOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }

    var showAddSheet by remember { mutableStateOf(false) }
    val unresolved by viewModel.unresolvedSms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khata") },
                actions = {
                    BadgedBox(badge = { if (unresolved.isNotEmpty()) Badge { Text("${unresolved.size}") } }) {
                        IconButton(onClick = { navController.navigate("review_sms") }) {
                            Icon(Icons.Filled.MarkChatUnread, contentDescription = "Review SMS")
                        }
                    }
                }
            )
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "dashboard", modifier = Modifier.padding(padding)) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("transactions") { TransactionsScreen(viewModel) }
            composable("budgets") { BudgetsScreen(viewModel) }
            composable("goals") { GoalsScreen(viewModel) }
            composable("reports") { ReportsScreen(viewModel) }
            composable("review_sms") { ReviewSmsScreen(viewModel) }
        }
    }

    if (showAddSheet) {
        AddTransactionSheet(viewModel = viewModel, onDismiss = { showAddSheet = false })
    }
}
