package com.meetozan.quickanimation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meetozan.quick_animation_ext.ExpandDirection
import com.meetozan.quick_animation_ext.FlipAxis
import com.meetozan.quick_animation_ext.FlipSide
import com.meetozan.quick_animation_ext.SlideInLeftFadeIn
import com.meetozan.quick_animation_ext.SlideInRightFadeIn
import com.meetozan.quick_animation_ext.circularReveal
import com.meetozan.quick_animation_ext.clickBounceEffect
import com.meetozan.quick_animation_ext.elasticOverscroll
import com.meetozan.quick_animation_ext.expandCollapse
import com.meetozan.quick_animation_ext.floatingEffect
import com.meetozan.quick_animation_ext.heartBeatEffect
import com.meetozan.quick_animation_ext.parallax
import com.meetozan.quick_animation_ext.scaleUpDown
import com.meetozan.quick_animation_ext.shakeHorizontal
import com.meetozan.quick_animation_ext.shakeVertical
import com.meetozan.quick_animation_ext.shimmerEffect
import com.meetozan.quick_animation_ext.spin
import com.meetozan.quick_animation_ext.touchWaveEffect
import com.meetozan.quick_animation_ext.wiggle
import kotlinx.coroutines.delay

@Composable
fun QuickAnimationDemo() {

    val scroll = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Elastic Overscroll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .elasticOverscroll(
                    overscrollAction = {
                        Toast
                            .makeText(
                                context,
                                "Overscrolled",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                )
                .background(Color(0xFFBBDEFB)),
            contentAlignment = Alignment.Center
        ) {
            Text("Drag Me ↓", fontSize = 20.sp)
        }

        // Touch Wave Effect
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.Black)
                .touchWaveEffect(
                    waveColor = Color.White.copy(alpha = 0.4f),
                    radiusMax = 120f
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Touch Wave", color = Color.White)
        }

        // Parallax Scrolling with Elastic Overscroll
        var overscrollOffset by remember { mutableFloatStateOf(0f) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .parallax(overscrollOffset.toInt())
                .background(Color(0xFFBBDEFB)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Parallax Scroll", fontSize = 20.sp)
        }

        // Circular Reveal
        var showContent by remember { mutableStateOf(false) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) { Text("Toggle Reveal") }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .circularReveal(trigger = showContent)
                    .background(Color.Green)
            ) {
                Text(
                    "Circular Reveal",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Expand / Collapse
        var expanded by remember { mutableStateOf(false) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { expanded = !expanded }) { Text("Toggle Expand") }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Green)
                    .expandCollapse(expanded, direction = ExpandDirection.VERTICAL)
                    .padding(12.dp)
            ) {
                Text(
                    "Expandable Content\nLine 2\nLine 3",
                    color = Color.White
                )
            }
        }

        // 3D Flip Side
        var flip by remember { mutableStateOf(false) }
        FlipSide(
            trigger = flip,
            width = 140.dp,
            height = 140.dp,
            axis = FlipAxis.X,
            repeatCount = 1,
            clockwise = true,
            front = {
                Box(
                    Modifier
                        .background(Color.Magenta)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Front", color = Color.White)
                }
            },
            back = {
                Box(
                    Modifier
                        .background(Color.Cyan)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Back", color = Color.White)
                }
            }
        )
        Button(onClick = { flip = !flip }) { Text("Flip Side") }

        // Spin Example
        var spinTrigger by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Yellow)
                .spin(trigger = spinTrigger, durationMillis = 800, repeatCount = 1),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { spinTrigger = !spinTrigger }) {
                Text("Spin", color = Color.White)
            }
        }

        // Floating Box
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Cyan)
                .floatingEffect(
                    offset = 14f,
                    floatMillis = 500,
                    repeatCount = 6,
                    isInfinite = false
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Floating", color = Color.Black)
        }

        // Wiggle Box
        var wiggleTrigger by remember { mutableStateOf(false) }

        LaunchedEffect(wiggleTrigger) {
            while (wiggleTrigger) {
                delay(1000L)
                wiggleTrigger = !wiggleTrigger
            }
        }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Red)
                .wiggle(
                    enabled = wiggleTrigger,
                    rotationAngle = 8f,
                    durationMillis = 1000,
                    wiggleSpeed = 3f
                ),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { wiggleTrigger = !wiggleTrigger }) {
                Text(
                    "Wiggle",
                    color = Color.White
                )
            }
        }

        // Click Bounce
        var bounceTrigger by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Magenta)
                .clickBounceEffect(scaleDown = 0.85f),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { bounceTrigger = !bounceTrigger }) {
                Text(
                    "Bounce",
                    color = Color.White
                )
            }
        }

        // Shake Horizontal
        var shakeTrigger by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Gray)
                .shakeHorizontal(trigger = shakeTrigger, intensity = 12f),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { shakeTrigger = !shakeTrigger }) {
                Text(
                    "Shake H",
                    color = Color.White
                )
            }
        }

        // Shake Vertical
        var shakeTriggerVertical by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Black)
                .shakeVertical(trigger = shakeTriggerVertical, intensity = 12f),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { shakeTriggerVertical = !shakeTriggerVertical }) {
                Text(
                    "Shake V",
                    color = Color.White
                )
            }
        }

        // Shimmer Effect
        var shimmerTrigger by remember { mutableStateOf(true) }

        LaunchedEffect(shimmerTrigger) {
            delay(3000L)
            shimmerTrigger = !shimmerTrigger
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Cyan)
                .shimmerEffect(shimmerTrigger),
            contentAlignment = Alignment.Center
        ) {
            Text("Shimmer", color = Color.DarkGray)
        }

        // Scale Up / Down
        var scaleUp by remember { mutableStateOf(false) }

        LaunchedEffect(scaleUp) {
            while (scaleUp) {
                delay(800L)
                scaleUp = !scaleUp
            }
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Green)
                .scaleUpDown(scaleUp, scaleUp = 1.3f),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { scaleUp = !scaleUp }) { Text("Scale", color = Color.White) }
        }

        // Heartbeat Effect

        var heartbeatTrigger by remember { mutableStateOf(false) }

        LaunchedEffect(heartbeatTrigger) {
            while (heartbeatTrigger) {
                delay(1200L)
                heartbeatTrigger = !heartbeatTrigger
            }
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Red)
                .heartBeatEffect(heartbeatTrigger, scaleUp = 1.4f, durationMillis = 600),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { heartbeatTrigger = !heartbeatTrigger }) {
                Text("Heartbeat", color = Color.White)
            }
        }

        // Slide In Right Fade In

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {

            var slideInRightTrigger by remember { mutableStateOf(false) }
            var slideInLeftTrigger by remember { mutableStateOf(false) }

            Button(onClick = { slideInRightTrigger = !slideInRightTrigger }) {
                Text("Slide In Right")
            }

            // SlideInRightFadeIn
            SlideInRightFadeIn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                trigger = slideInRightTrigger,
                durationMillis = 1000,
                delayMillis = 0,
                isHaveExitAnimation = true,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.6f)
                        .background(Color.Green),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Slide In Right", color = Color.White, fontSize = 18.sp)
                }
            }

            Button(onClick = { slideInLeftTrigger = !slideInLeftTrigger }) {
                Text("Slide In Left")
            }

            SlideInLeftFadeIn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                trigger = slideInLeftTrigger,
                durationMillis = 1000,
                delayMillis = 0,
                isHaveExitAnimation = true,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.6f)
                        .background(Color.Green),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Slide In Left", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}