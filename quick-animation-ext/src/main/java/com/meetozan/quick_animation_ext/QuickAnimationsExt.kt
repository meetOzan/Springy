/*
 * Copyright 2026 The Android Open Source Project
 * Created by meetOzan on 04.02.2026
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meetozan.quick_animation_ext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

/**
 * A modifier that adds a bounce effect when the composable is pressed.
 *
 * Recommended for **interactive** elements such as `Button`, `IconButton` or
 * any composable that already has a click / tap behavior (e.g. `Modifier.clickable`).
 * You can also use it on other content, but UX‑wise it makes the most sense
 * when the element is actually clickable.
 *
 * @param scaleDown The scale factor to apply when the composable is pressed. Default is 0.90f (90% of original size).
 *
 * Usage (recommended on clickable components):
 * ```
 * Button(
 *     modifier = Modifier.clickBounceEffect(),
 *     onClick = { /* Handle click */ }
 * ) {
 *     Text("Click Me")
 * }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.clickBounceEffect(
    scaleDown: Float = 0.90f
) = composed {

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        label = "BounceAnimation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true

                    val up = waitForUpOrCancellation()
                    isPressed = false

                    if (up == null) {
                        continue
                    }
                }
            }
        }
}

/**
 *  A modifier that adds a shake effect to the composable with basing [Modifier.baseShake].
 *
 *  The shake can be either **vertical** [Modifier.shakeVertical].
 *
 *  Best suited for **error / invalid input** feedback, or to draw attention
 *  to a specific area of the UI (e.g. a form field or card).
 *
 * @param trigger An optional parameter that can be used to trigger the shake animation. Whenever this value changes, the shake animation will be executed.
 * @param durationMillis The duration of the shake animation in milliseconds. Default is 500ms
 * @param intensity The intensity of the shake, which determines how far the composable will move during the shake. Default is 20f (20 pixels).
 * @param onAnimationEnd An optional callback that will be invoked when the shake animation ends.
 * Usage:
 * ```
 * Box(
 *    modifier = Modifier
 *    .shakeHorizontal(trigger = shakeTrigger)
 *    .size(100.dp)
 *    .background(Color.Red)
 *    )
 *    Button(onClick = { shakeTrigger = !shakeTrigger }) {
 *    Text("Shake")
 *    }
 * ```
 * Note: The `trigger` parameter can be any value (e.g., a boolean, an integer, etc.) that changes when you want to trigger the shake animation. In the example above, we toggle a boolean `shakeTrigger` to trigger the shake effect when the button is clicked.
 *
 */
fun Modifier.shakeHorizontal(
    trigger: Any? = null,
    durationMillis: Int = 500,
    intensity: Float = 20f,
    onAnimationEnd: (() -> Unit)? = null
): Modifier = this.baseShake(
    trigger = trigger,
    durationMillis = durationMillis,
    intensity = intensity,
    isHorizontal = true,
    onAnimationEnd = onAnimationEnd
)


/**
 * A modifier that adds a shake effect to the composable with basing [Modifier.baseShake].
 *
 * The shake can be either **horizontal** [Modifier.shakeHorizontal].
 *
 * Typically used on components that represent a **row or block of content**
 * (e.g. cards, containers) to signal attention or error.
 *
 * @param trigger An optional parameter that can be used to trigger the shake animation. Whenever this value changes, the shake animation will be executed.
 * @param durationMillis The duration of the shake animation in milliseconds. Default is 500ms
 * @param intensity The intensity of the shake, which determines how far the composable will move during the shake. Default is 20f (20 pixels).
 * @param onAnimationEnd An optional callback that will be invoked when the shake animation ends.
 * Usage:
 * ```
 * Box(
 *    modifier = Modifier
 *    .shakeVertical(trigger = shakeTrigger)
 *    .size(100.dp)
 *    .background(Color.Red)
 *    )
 *    Button(onClick = { shakeTrigger = !shakeTrigger }) {
 *    Text("Shake")
 *    }
 * ```
 * Note: The `trigger` parameter can be any value (e.g., a boolean, an integer, etc.) that changes when you want to trigger the shake animation. In the example above, we toggle a boolean `shakeTrigger` to trigger the shake effect when the button is clicked.
 *
 */
fun Modifier.shakeVertical(
    trigger: Any? = null,
    durationMillis: Int = 500,
    intensity: Float = 20f,
    onAnimationEnd: (() -> Unit)? = null
): Modifier = this.baseShake(
    trigger = trigger,
    durationMillis = durationMillis,
    intensity = intensity,
    isHorizontal = false,
    onAnimationEnd = onAnimationEnd
)

@Suppress("AvoidComposed")
private fun Modifier.baseShake(
    trigger: Any?,
    durationMillis: Int,
    intensity: Float,
    isHorizontal: Boolean,
    onAnimationEnd: (() -> Unit)?
): Modifier = composed {

    val animatable = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger != null) {
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    this.durationMillis = durationMillis
                    0f at 0
                    -intensity at (durationMillis * 0.2f).toInt()
                    intensity at (durationMillis * 0.4f).toInt()
                    -intensity / 2 at (durationMillis * 0.6f).toInt()
                    intensity / 2 at (durationMillis * 0.8f).toInt()
                    0f at durationMillis
                }
            )
            onAnimationEnd?.invoke()
        }
    }

    this.graphicsLayer {
        if (isHorizontal) {
            translationX = animatable.value
        } else {
            translationY = animatable.value
        }
    }
}

fun Modifier.scaleUpDown(
    trigger: Any? = null,
    durationMillis: Int = 600,
    scaleUp: Float = 1.20f,
    scaleDown: Float = 1f,
    isInfinite: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
): Modifier = this.baseHeartBeat(
    trigger = trigger,
    durationMillis = durationMillis,
    scaleUp = scaleUp,
    scaleDown = scaleDown,
    isInfinite = isInfinite,
    isOnRealHeartBeatEffect = false,
    onAnimationEnd = onAnimationEnd
)

fun Modifier.heartBeatEffect(
    trigger: Any? = null,
    durationMillis: Int = 600,
    scaleUp: Float = 1.20f,
    scaleDown: Float = 1f,
    isInfinite: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
): Modifier = this.baseHeartBeat(
    trigger = trigger,
    durationMillis = durationMillis,
    scaleUp = scaleUp,
    scaleDown = scaleDown,
    isInfinite = isInfinite,
    isOnRealHeartBeatEffect = true,
    onAnimationEnd = onAnimationEnd
)


@Suppress("AvoidComposed")
private fun Modifier.baseHeartBeat(
    trigger: Any? = null,
    durationMillis: Int = 600,
    scaleUp: Float = 1.20f,
    scaleDown: Float = 1f,
    isInfinite: Boolean = false,
    isOnRealHeartBeatEffect: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
): Modifier = composed {
    val scale = remember { Animatable(1f) }

    val heartBeatEasing = CubicBezierEasing(
        0.4f, 0.0f,
        0.2f, 1.0f
    )
    LaunchedEffect(trigger) {
        if (isInfinite) {
            while (true) {
                if (isOnRealHeartBeatEffect) {
                    scale.animateTo(scaleUp, tween(durationMillis / 5, easing = heartBeatEasing))
                    scale.animateTo(scaleDown, tween(durationMillis / 3, easing = heartBeatEasing))
                    delay(120)
                    scale.animateTo(
                        scaleUp - (scaleUp / 14),
                        tween(durationMillis / 6, easing = heartBeatEasing)
                    )
                    scale.animateTo(scaleDown, tween(durationMillis / 4, easing = heartBeatEasing))
                    delay(600)
                } else {
                    scale.animateTo(
                        targetValue = scaleUp,
                        animationSpec = tween(durationMillis / 2)
                    )
                    scale.animateTo(
                        targetValue = scaleDown,
                        animationSpec = tween(durationMillis / 2)
                    )
                }
            }
        } else {
            if (isOnRealHeartBeatEffect) {
                scale.animateTo(scaleUp, tween(durationMillis / 5, easing = heartBeatEasing))
                scale.animateTo(scaleDown, tween(durationMillis / 3, easing = heartBeatEasing))
                delay(120)
                scale.animateTo(
                    scaleUp - (scaleUp / 14),
                    tween(durationMillis / 6, easing = heartBeatEasing)
                )
                scale.animateTo(scaleDown, tween(durationMillis / 4, easing = heartBeatEasing))
                delay(600)
            } else {
                scale.animateTo(
                    targetValue = scaleUp,
                    animationSpec = tween(
                        durationMillis / 2,
                    )
                )
                scale.animateTo(
                    targetValue = scaleDown,
                    animationSpec = tween(durationMillis / 2)
                )
            }
        }
        onAnimationEnd?.invoke()
    }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
}

/**
 * A modifier that adds a wiggle effect to the composable.
 * The composable will rotate back and forth around the Z-axis, creating a wiggle effect.
 *
 * @param enabled An optional parameter that can be used to trigger the wiggle animation. Whenever this value changes to true, the wiggle animation will be executed.
 * @param durationMillis The duration of the wiggle animation in milliseconds. Default is 300
 * @param rotationAngle The maximum rotation angle in degrees for the wiggle effect. Default is 5f (5 degrees).
 * Usage:
 * ```
 * Button(
 *     modifier = Modifier.wiggle(enabled = isWiggling),
 *     onClick = { isWiggling = true }
 * ) {
 *     Text("Wiggle Me")
 * }
 * ```
 * Note: The `enabled` parameter can be any value (e.g., a boolean, an integer, etc.) that changes when you want to trigger the wiggle animation. In the example above, we set a boolean `isWiggling` to true to trigger the wiggle effect
 */
@Suppress("AvoidComposed")
fun Modifier.wiggle(
    enabled: Boolean,
    durationMillis: Int = 300,
    rotationAngle: Float = 5f
): Modifier = composed {
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            rotationAnim.snapTo(0f)
            rotationAnim.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    this.durationMillis = durationMillis
                    0f at 0
                    -rotationAngle at (durationMillis * 0.25f).toInt() // Sola yat
                    rotationAngle at (durationMillis * 0.75f).toInt()  // Sağa yat
                    0f at durationMillis
                }
            )
        }
    }

    this.graphicsLayer {
        rotationZ = rotationAnim.value
    }
}

enum class SpinSide {
    CLOCKWISE,
    COUNTERCLOCKWISE
}

/**
 * A modifier that adds a spin animation to the composable.
 *
 * @param trigger An optional parameter that can be used to trigger the spin animation. Whenever this value changes, the spin animation will be executed.
 * @param durationMillis The duration of one full rotation in milliseconds. Default is 1000ms (1 second).
 * @param repeatCount The number of times the spin animation should repeat. Default is 1 (no repetition).
 * @param rotationAngle The angle in degrees for one full rotation. Default is 360f (full circle).
 * @param isInfinite If true, the spin animation will repeat indefinitely. Default is false.
 * @param side The direction of the spin, either clockwise or counterclockwise that managed with [SpinSide].
 * Default is [SpinSide.CLOCKWISE].
 *
 * Usage:
 * ```
 * Button(
 *     modifier = Modifier.spin(trigger = spinTrigger),
 *     onClick = { spinTrigger = !spinTrigger }
 * ) {
 *     Text("Spin Me")
 * }
 * ```
 *
 * Note: The `trigger` parameter can be any value (e.g., a boolean, an integer, etc.) that changes when you want to trigger the spin animation. In the example above, we toggle a boolean `spinTrigger` to trigger the spin effect when the button is clicked.
 */
@Suppress("AvoidComposed")
fun Modifier.spin(
    trigger: Any? = null,
    durationMillis: Int = 1000,
    repeatCount: Int = 1,
    rotationAngle: Float = 360f,
    isInfinite: Boolean = false,
    side: SpinSide = SpinSide.CLOCKWISE,
): Modifier = composed {
    val rotationAnim = remember { Animatable(0f) }

    suspend fun spinAnimation(target: Float) {
        rotationAnim.snapTo(0f)
        rotationAnim.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis, easing = LinearEasing)
        )
    }

    LaunchedEffect(trigger) {
        if (trigger != null) {
            val target = if (side == SpinSide.CLOCKWISE) rotationAngle else -rotationAngle
            if (isInfinite) {
                while (true) {
                    spinAnimation(target)
                }
            } else {
                repeat(repeatCount) {
                    spinAnimation(target)
                }
            }
        }
    }

    this.graphicsLayer {
        rotationZ = rotationAnim.value
    }
}

@Composable
fun SlideInRightFadeIn(
    modifier: Modifier = Modifier,
    trigger: Any? = Unit,
    durationMillis: Int = 300,
    delayMillis: Int = 0,
    easing: Easing = LinearOutSlowInEasing,
    contentAlign: Alignment = Alignment.Center,
    isHaveExitAnimation: Boolean = false,
    content: @Composable () -> Unit
) {

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier.fillMaxWidth(),
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = easing
            )
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMillis)
        ),
        exit = if (isHaveExitAnimation) {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = durationMillis)
            ) + fadeOut()
        } else {
            ExitTransition.None
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = contentAlign
        ) {
            content.invoke()
        }
    }
}

@Composable
fun SlideInLeftFadeIn(
    modifier: Modifier = Modifier,
    trigger: Any? = Unit,
    durationMillis: Int = 300,
    delayMillis: Int = 0,
    easing: Easing = LinearOutSlowInEasing,
    contentAlign: Alignment = Alignment.Center,
    isHaveExitAnimation: Boolean = false,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = easing
            )
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMillis)
        ),
        exit = if (isHaveExitAnimation) {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = durationMillis)
            ) + fadeOut()
        } else {
            ExitTransition.None
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = contentAlign
        ) {
            content.invoke()
        }
    }
}

@Suppress("AvoidComposed")
fun Modifier.shimmerEffect(
    endTrigger: Boolean,
    shimmerColor: Color = Color.LightGray.copy(alpha = 0.6f),
    baseColor: Color = Color.LightGray.copy(alpha = 0.2f),
    durationMillis: Int = 1000,
    shape: Shape = RoundedCornerShape(4)
): Modifier = composed {

    val alpha by animateFloatAsState(
        targetValue = if (endTrigger) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "ShimmerFadeOut"
    )

    if (endTrigger && alpha == 0f) {
        return@composed this
    }

    var size by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "Shimmer")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffsetX"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor.copy(alpha = baseColor.alpha * alpha),
            shimmerColor.copy(alpha = shimmerColor.alpha * alpha),
            baseColor.copy(alpha = baseColor.alpha * alpha)
        ),
        start = Offset(startOffsetX, 0f),
        end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
    )

    this
        .onGloballyPositioned { size = it.size }
        .clip(shape)
        .background(brush)
}