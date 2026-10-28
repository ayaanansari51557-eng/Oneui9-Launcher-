package com.oneui9.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LauncherViewModel = viewModel()
            val context = applicationContext
            
            LaunchedEffect(Unit) {
                viewModel.loadApps(context.packageManager)
            }

            var isQuickPanelOpen by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF080808))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount > 30) {
                                isQuickPanelOpen = true
                            }
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "One UI 9 Home",
                        color = Color.White,
                        fontSize = 28.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.appList) { app ->
                            AppItemView(app = app) {
                                app.launchIntent?.let { context.startActivity(it) }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isQuickPanelOpen,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    QuickPanelScreen {
                        isQuickPanelOpen = false
                    }
                }
            }
        }
    }
}

@Composable
fun AppItemView(app: AppModel, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.title.take(1).uppercase(),
                color = Color.Cyan,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.title,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun QuickPanelScreen(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    Color(0xFF141414),
                    shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
                .padding(28.dp)
        ) {
            Text("One UI 9 Control Center", color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickTile("Wi-Fi", true)
                QuickTile("Bluetooth", true)
                QuickTile("Sound", false)
                QuickTile("Dark Mode", true)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Brightness Slider", color = Color.Gray, fontSize = 12.sp)
            var sliderPos by remember { mutableStateOf(0.6f) }
            Slider(value = sliderPos, onValueChange = { sliderPos = it })
        }
    }
}

@Composable
fun QuickTile(label: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = if (active) Color(0xFF0066FF) else Color(0xFF262626),
            modifier = Modifier.size(60.dp)
        ) {}
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.White, fontSize = 11.sp)
    }
}
