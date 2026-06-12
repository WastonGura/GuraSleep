package com.example.gurasleep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gurasleep.domain.detector.SleepDetector
import com.example.gurasleep.viewmodel.CaptureViewModel
import com.example.gurasleep.viewmodel.SleepViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val captureViewModel: CaptureViewModel = viewModel()
            val sleepViewModel: SleepViewModel = viewModel()

            // 注入 SleepDetector（需要 Context）
            if (sleepViewModel.sleepDetector == null) {
                sleepViewModel.sleepDetector = SleepDetector(this@MainActivity)
            }

            GuraSleepApp(
                captureViewModel = captureViewModel,
                sleepViewModel = sleepViewModel
            )
        }
    }
}
