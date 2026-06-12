package com.example.gurasleep

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.DockTab
import com.example.gurasleep.ui.components.DockBar
import com.example.gurasleep.ui.screens.CaptureScreen
import com.example.gurasleep.ui.screens.SleepScreen
import com.example.gurasleep.ui.theme.DeepPurple950
import com.example.gurasleep.ui.theme.GuraSleepTheme
import com.example.gurasleep.viewmodel.CaptureViewModel

/**
 * 顶层 Composable
 *  导航: Dock 栏切换「捕获」「睡眠」两个标签页
 */
@Composable
fun GuraSleepApp(captureViewModel: CaptureViewModel) {
    val dockState = captureViewModel.dockState

    GuraSleepTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepPurple950)
        ) {
            // 内容区（根据 Dock 标签切换）
            Crossfade(
                targetState = dockState.activeTab,
                animationSpec = tween(350),
                label = "pageCrossfade"
            ) { tab ->
                when (tab) {
                    DockTab.CAPTURE -> CaptureScreen()
                    DockTab.SLEEP -> SleepScreen()
                }
            }

            // 悬浮 Dock 栏（固定在底部）
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DockBar(
                    dockState = dockState,
                    onTabSelected = { captureViewModel.selectTab(it) }
                )
            }
        }
    }
}
