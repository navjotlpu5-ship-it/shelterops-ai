package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.ShelterOpsMainApp
import com.example.ui.theme.CommandDarkBg
import com.example.ui.theme.ShelterOpsTheme
import com.example.viewmodel.ShelterOpsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShelterOpsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShelterOpsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CommandDarkBg
                ) {
                    ShelterOpsMainApp(viewModel = viewModel)
                }
            }
        }
    }
}
