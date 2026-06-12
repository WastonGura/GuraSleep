package com.example.gurasleep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gurasleep.viewmodel.CaptureViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val captureViewModel: CaptureViewModel = viewModel()

            GuraSleepApp(captureViewModel = captureViewModel)
        }
    }
}
