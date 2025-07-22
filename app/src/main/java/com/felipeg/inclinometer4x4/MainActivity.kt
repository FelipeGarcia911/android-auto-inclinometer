package com.felipeg.inclinometer4x4

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.felipeg.inclinometer4x4.ui.theme.Inclinometer4x4Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Inclinometer4x4Theme {
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
}

enum class Screen {
    Dashboard,
    About
}

@Composable
fun MainScreen(
    viewModel: SensorViewModel,
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit
) {
    val orientation by viewModel.orientationState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(orientation) {
        (context as? Activity)?.requestedOrientation = orientation
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.Dashboard -> DashboardScreen(viewModel, onScreenChange = onScreenChange)
            Screen.About -> AboutScreen()
        }
    }
}
