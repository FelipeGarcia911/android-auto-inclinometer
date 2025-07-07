package com.felipeg.inclinometer4x4

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.presentation.ui.AboutScreen
import com.felipeg.inclinometer4x4.presentation.ui.DashboardScreen
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sensorRepo: com.felipeg.common.SensorRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: SensorViewModel = hiltViewModel()
            var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

            MainScreen(
                viewModel = viewModel,
                currentScreen = currentScreen,
                onScreenChange = { currentScreen = it }
            )
        }
    }
}

enum class Screen {
    Dashboard,
    About
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SensorViewModel,
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit
) {
    val orientation by viewModel.orientationState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(orientation) {
        (context as? Activity)?.requestedOrientation = orientation
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inclinometer 4x4") },
                navigationIcon = {
                    if (currentScreen == Screen.About) {
                        IconButton(onClick = { onScreenChange(Screen.Dashboard) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentScreen == Screen.Dashboard) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Calibrate") },
                                onClick = {
                                    viewModel.onCalibrate()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Toggle Orientation") },
                                onClick = {
                                    viewModel.toggleOrientation()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = {
                                    onScreenChange(Screen.About)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel)
                Screen.About -> AboutScreen()
            }
        }
    }
}