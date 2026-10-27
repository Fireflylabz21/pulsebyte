package com.fireflylabz.taptap.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.taptap.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.15f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (progress < 1f) {
            delay(35L)
            val elapsed = (System.currentTimeMillis() - startTime).toFloat()
            progress = (elapsed / 1400f).coerceIn(0.15f, 1f)
        }
        delay(100L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030508),
                        Color(0xFF070B14),
                        Color(0xFF0C1222)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Logo Image matching the attached design
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "TapTap Logo",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Name "TapTap" with Gold styling
            Text(
                text = "TapTap",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                    letterSpacing = 1.5.sp
                ),
                color = Color(0xFFFFD700)
            )

            Spacer(modifier = Modifier.height(160.dp))

            // LOADING... text
            Text(
                text = "LOADING...",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.0.sp,
                    fontSize = 12.sp
                ),
                color = Color(0xFFC5A059)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Glowing Progress Bar matching the attached image design
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1B160B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF997A25),
                                    Color(0xFFFFD700),
                                    Color(0xFFFFEE88)
                                )
                            )
                        )
                )
            }
        }
    }
}
