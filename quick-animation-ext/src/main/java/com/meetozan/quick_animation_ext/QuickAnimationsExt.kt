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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

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
 * // Primary action button with press feedback
 * Button(
 *     modifier = Modifier.clickBounceEffect(),
 *     onClick = { viewModel.submit() }
 * ) { Text("Submit") }
 *
 * // Icon button (e.g. FAB, toolbar)
 * IconButton(
 *     modifier = Modifier.clickBounceEffect(scaleDown = 0.92f),
 *     onClick = { onFavoriteClick() }
 * ) { Icon(Icons.Default.Favorite, null) }
 *
 * // Custom clickable area
 * Box(
 *     modifier = Modifier
 *         .clickable { onClick() }
 *         .clickBounceEffect()
 * ) { /* content */ }
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
 * // Form validation error: shake the field when input is invalid
 * var showError by remember { mutableStateOf(false) }
 * OutlinedTextField(
 *     value = email,
 *     onValueChange = { email = it },
 *     modifier = Modifier.shakeHorizontal(
 *         trigger = showError,
 *         onAnimationEnd = { showError = false }
 *     ),
 *     isError = showError
 * )
 * Button(onClick = { if (!isValid(email)) showError = true else submit() }) {
 *     Text("Submit")
 * }
 *
 * // Shake a card when action is denied
 * var denyTrigger by remember { mutableStateOf(0) }
 * Card(modifier = Modifier.shakeHorizontal(trigger = denyTrigger)) {
 *     Button(onClick = { if (!canProceed) denyTrigger++ else proceed() }) { Text("Proceed") }
 * }
 * ```
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
 * // Vertical list item or card: signal error or "no" feedback
 * var shakeKey by remember { mutableStateOf(0) }
 * Card(modifier = Modifier.shakeVertical(trigger = shakeKey, intensity = 12f)) {
 *     Column {
 *         Text("Item")
 *         Button(onClick = { if (denied) shakeKey++ else accept() }) { Text("Accept") }
 *     }
 * }
 * ```
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

/**
 * Scales the composable up then back down. Use for one-off emphasis (e.g. star, badge).
 *
 * @param trigger When this value changes, the animation runs once (or loops if [isInfinite] is true).
 * @param durationMillis Total duration for scale-up + scale-down. Default 600ms.
 * @param scaleUp Peak scale (e.g. 1.2f = 120%). Default 1.20f.
 * @param scaleDown Rest scale. Default 1f.
 * @param isInfinite If true, repeats indefinitely. Default false.
 * @param onAnimationEnd Called when a single run finishes (ignored when [isInfinite] is true).
 *
 * Usage:
 * ```
 * // Star / badge pulse on toggle
 * var liked by remember { mutableStateOf(false) }
 * Icon(
 *     imageVector = Icons.Default.Star,
 *     contentDescription = null,
 *     modifier = Modifier.scaleUpDown(trigger = liked, scaleUp = 1.3f)
 * )
 * IconButton(onClick = { liked = !liked }) { /* icon above */ }
 *
 * // One-time “new” badge pop
 * var hasSeen by remember { mutableStateOf(false) }
 * Badge(modifier = Modifier.scaleUpDown(trigger = !hasSeen, onAnimationEnd = { hasSeen = true }))
 * ```
 */
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

/**
 * Heartbeat-style scale animation (double beat with easing). Ideal for like/favorite actions.
 *
 * @param trigger When this value changes, the heartbeat runs once (or loops if [isInfinite] is true).
 * @param durationMillis Base duration for the beat. Default 600ms.
 * @param scaleUp Peak scale. Default 1.20f.
 * @param scaleDown Rest scale. Default 1f.
 * @param isInfinite If true, repeats indefinitely. Default false.
 * @param onAnimationEnd Called when one run finishes (ignored when [isInfinite] is true).
 *
 * Usage:
 * ```
 * // Like button: single heartbeat on tap
 * var liked by remember { mutableStateOf(false) }
 * Icon(
 *     imageVector = Icons.Default.Favorite,
 *     contentDescription = null,
 *     modifier = Modifier.heartBeatEffect(trigger = liked),
 *     tint = if (liked) Color.Red else Color.Gray
 * )
 * IconButton(onClick = { liked = !liked }) { /* icon above */ }
 *
 * // Infinite subtle pulse (e.g. “live” indicator)
 * Box(modifier = Modifier.heartBeatEffect(trigger = Unit, isInfinite = true, scaleUp = 1.08f))
 * ```
 */
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
 * @param enabled An mandatory parameter that used for trigger the wiggle animation. Whenever this value changes to true, the wiggle animation will be executed.
 * @param durationMillis The duration of the wiggle animation in milliseconds. Default is 300
 * @param rotationAngle The maximum rotation angle in degrees for the wiggle effect. Default is 5f (5 degrees).
 * @param wiggleSpeed A multiplier that controls the speed of the wiggle animation. Higher values will make the wiggle faster, while lower values will make it slower. Default is 1f (normal speed).
 * Usage:
 * ```
 * // Draw attention to a CTA (e.g. “Complete profile”)
 * var wiggle by remember { mutableStateOf(false) }
 * Button(
 *     modifier = Modifier.wiggle(enabled = wiggle, rotationAngle = 6f),
 *     onClick = { navigate() }
 * ) { Text("Continue") }
 * LaunchedEffect(Unit) { delay(500); wiggle = true }
 *
 * // Wiggle on validation error
 * var shouldWiggle by remember { mutableStateOf(false) }
 * FilledTonalButton(
 *     modifier = Modifier.wiggle(enabled = shouldWiggle),
 *     onClick = { if (!isValid) shouldWiggle = true else submit() }
 * ) { Text("Submit") }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.wiggle(
    enabled: Boolean,
    durationMillis: Int = 300,
    rotationAngle: Float = 5f,
    wiggleSpeed: Float = 1f
): Modifier = composed {

    val rotationAnim = remember { Animatable(0f) }

    val adjustedDuration = (durationMillis / wiggleSpeed)
        .toInt()
        .coerceAtLeast(1)

    LaunchedEffect(enabled, wiggleSpeed) {
        if (enabled) {
            rotationAnim.snapTo(0f)
            rotationAnim.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    this.durationMillis = adjustedDuration
                    0f at 0
                    -rotationAngle at (adjustedDuration * 0.25f).toInt()
                    rotationAngle at (adjustedDuration * 0.75f).toInt()
                    0f at adjustedDuration
                }
            )
        }
    }

    graphicsLayer {
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
 * // Refresh icon: one full rotation on pull-to-refresh
 * var refreshKey by remember { mutableStateOf(0) }
 * Icon(
 *     imageVector = Icons.Default.Refresh,
 *     contentDescription = null,
 *     modifier = Modifier.spin(trigger = refreshKey, durationMillis = 500)
 * )
 * Button(onClick = { refreshKey++; viewModel.refresh() }) { Text("Refresh") }
 *
 * // Loading spinner: infinite rotation
 * Icon(
 *     imageVector = Icons.Default.Refresh,
 *     contentDescription = "Loading",
 *     modifier = Modifier.spin(trigger = Unit, isInfinite = true, durationMillis = 800)
 * )
 *
 * // 3 spins then stop (e.g. success celebration)
 * var celebrate by remember { mutableStateOf(false) }
 * Icon(..., modifier = Modifier.spin(trigger = celebrate, repeatCount = 3))
 * LaunchedEffect(success) { if (success) celebrate = !celebrate }
 * ```
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

/**
 * Composable that animates its content in from the **right** with slide + fade.
 * Toggle [trigger] to replay enter/exit (when [isHaveExitAnimation] is true).
 *
 * @param modifier Applied to the [AnimatedVisibility] container.
 * @param trigger When this value changes (after first composition), visibility toggles.
 * @param durationMillis Duration of enter/exit. Default 300.
 * @param delayMillis Delay before starting enter. Default 0.
 * @param easing Easing for the slide. Default [LinearOutSlowInEasing].
 * @param contentAlign Alignment of the content box.
 * @param isHaveExitAnimation If true, exit uses slide-out + fade; otherwise content disappears without animation.
 *
 * Usage:
 * ```
 * // One-time intro from the right (e.g. detail screen)
 * SlideInRightFadeIn(durationMillis = 400, delayMillis = 100) {
 *     Text("Welcome", style = MaterialTheme.typography.headlineMedium)
 * }
 *
 * // Toggle panel from the right
 * var visible by remember { mutableStateOf(false) }
 * SlideInRightFadeIn(trigger = visible, isHaveExitAnimation = true) {
 *     Card { /* panel content */ }
 * }
 * Button(onClick = { visible = !visible }) { Text("Toggle panel") }
 * ```
 */
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
    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(trigger) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = !isVisible
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

/**
 * Composable that animates its content in from the **left** with slide + fade.
 * Toggle [trigger] to replay enter/exit (when [isHaveExitAnimation] is true).
 *
 * @param modifier Applied to the [AnimatedVisibility] container.
 * @param trigger When this value changes (after first composition), visibility toggles.
 * @param durationMillis Duration of enter/exit. Default 300.
 * @param delayMillis Delay before starting enter. Default 0.
 * @param easing Easing for the slide. Default [LinearOutSlowInEasing].
 * @param contentAlign Alignment of the content box.
 * @param isHaveExitAnimation If true, exit uses slide-out + fade; otherwise content disappears without animation.
 *
 * Usage:
 * ```
 * // One-time intro from the left (e.g. onboarding step)
 * SlideInLeftFadeIn(durationMillis = 400, delayMillis = 50) {
 *     Column {
 *         Text("Step 1", style = MaterialTheme.typography.titleLarge)
 *         Text("Description here.")
 *     }
 * }
 *
 * // Toggle drawer/content from the left
 * var showDrawer by remember { mutableStateOf(false) }
 * SlideInLeftFadeIn(trigger = showDrawer, isHaveExitAnimation = true) {
 *     NavigationDrawerContent(...)
 * }
 * IconButton(onClick = { showDrawer = !showDrawer }) { Icon(Icons.Default.Menu, null) }
 * ```
 */
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
    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(trigger) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = !isVisible
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

/**
 * Draws a shimmer (skeleton) overlay that moves across the composable. Set [endTrigger] to true
 * to fade out and remove the shimmer (e.g. when data has loaded).
 *
 * @param endTrigger When true, shimmer fades out and the modifier effectively becomes a no-op.
 * @param shimmerColor Color of the bright band. Default light gray.
 * @param baseColor Color of the dim background. Default light gray (lower alpha).
 * @param durationMillis Duration of one full pass of the shimmer. Default 1000.
 * @param shape Shape used to clip the shimmer. Default [RoundedCornerShape(4)].
 *
 * Usage:
 * ```
 * // Skeleton placeholder while loading
 * var loaded by remember { mutableStateOf(false) }
 * Box(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .height(120.dp)
 *         .shimmerEffect(endTrigger = loaded)
 * ) { /* optional inner content */ }
 * LaunchedEffect(data) { loaded = data != null }
 *
 * // Card placeholder
 * Card(modifier = Modifier.shimmerEffect(endTrigger = !isLoading)) {
 *     if (!isLoading) Text("Content") else Box(Modifier.height(80.dp))
 * }
 * ```
 */
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

/**
 * Adds elastic (rubber-band) overscroll when the user drags past the content bounds.
 * Typically used on a scrollable container (e.g. Column inside verticalScroll).
 *
 * @param enabled When false, the modifier does nothing. Default true.
 * @param maxOffset Maximum drag offset in pixels. Default 120f.
 * @param stiffness Multiplier for drag (lower = stiffer). Default 0.15f.
 * @param overscrollAction Called when the user releases after overscrolling (e.g. trigger refresh).
 *
 * Usage:
 * ```
 * val scrollState = rememberScrollState()
 * Column(
 *     modifier = Modifier
 *         .verticalScroll(scrollState)
 *         .elasticOverscroll(
 *             maxOffset = 80f,
 *             overscrollAction = { viewModel.pullToRefresh() }
 *         )
 * ) { /* list content */ }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.elasticOverscroll(
    enabled: Boolean = true,
    maxOffset: Float = 120f,
    stiffness: Float = 0.15f,
    overscrollAction: () -> Unit = {}
): Modifier = composed {

    if (!enabled) return@composed this

    val offset = remember { Animatable(0f) }
    var dragState by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val pointerModifier = pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitFirstDown()
                var totalDrag = 0f

                do {
                    val event = awaitPointerEvent()
                    val drag = event.changes.first().positionChange().y

                    totalDrag += drag * stiffness
                    dragState = totalDrag.coerceIn(-maxOffset, maxOffset)

                    event.changes.forEach { it.consume() }
                } while (event.changes.any { it.pressed })

                scope.launch {
                    offset.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(300)
                    )

                    overscrollAction()
                }
            }
        }
    }

    LaunchedEffect(dragState) {
        if (dragState != 0f) {
            offset.snapTo(dragState)
        }
    }

    this
        .then(pointerModifier)
        .graphicsLayer {
            translationY = offset.value
        }
}

/**
 * Offsets the composable by [scrollValue] * [ratio] on the Y axis. Use inside a scroll layout
 * to create a parallax effect (e.g. header image moves slower than the list).
 *
 * @param scrollValue Current scroll position in pixels (e.g. from [ScrollState.value] or [LazyListState]).
 * @param ratio Fraction of scroll to apply (0.5f = move half as fast as scroll). Default 0.5f.
 *
 * Usage:
 * ```
 * val listState = rememberLazyListState()
 * Box(Modifier.fillMaxSize()) {
 *     Image(
 *         modifier = Modifier
 *             .fillMaxWidth()
 *             .parallax(listState.firstVisibleItemScrollOffset, ratio = 0.4f),
 *         imageUrl = headerUrl,
 *         contentScale = ContentScale.Crop
 *     )
 *     LazyColumn(state = listState) { items(...) }
 * }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.parallax(
    scrollValue: Int,
    ratio: Float = 0.5f
): Modifier = composed {
    graphicsLayer {
        translationY = scrollValue * ratio
    }
}

/**
 * Reveals or hides content with a circular clip expanding from the center (or closing toward it).
 * Use for dialogs, full-screen transitions, or “focus” effects.
 *
 * @param trigger When this value changes (after first run), animation runs: reveal if [isClose] is false, close if true.
 * @param durationMillis Duration of the circle expand/collapse. Default 600.
 * @param isClose If true, circle shrinks (content disappears); if false, circle grows (content appears). Default false.
 *
 * Usage:
 * ```
 * var showDialog by remember { mutableStateOf(false) }
 * Box(Modifier.circularReveal(trigger = showDialog, isClose = !showDialog)) {
 *     FullScreenContent(onDismiss = { showDialog = false })
 * }
 * Button(onClick = { showDialog = true }) { Text("Open") }
 *
 * // Reverse (close) animation
 * var visible by remember { mutableStateOf(true) }
 * Box(Modifier.circularReveal(trigger = visible, isClose = true)) { ... }
 * Button(onClick = { visible = false }) { Text("Close") }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.circularReveal(
    trigger: Boolean,
    durationMillis: Int = 600,
    isClose: Boolean = false
): Modifier = composed {

    var size by remember { mutableStateOf(IntSize.Zero) }
    val circularRadius = remember { Animatable(0f) }
    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(trigger, isClose) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }

        if (size != IntSize.Zero) {
            val maxRadius = hypot(size.width.toFloat(), size.height.toFloat())
            if (trigger && !isClose) {
                circularRadius.snapTo(0f)
                circularRadius.animateTo(
                    targetValue = maxRadius,
                    animationSpec = tween(durationMillis)
                )
            } else {
                circularRadius.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis)
                )
            }
        }
    }

    onGloballyPositioned { size = it.size }
        .graphicsLayer {
            clip = true
            shape = GenericShape { _, _ ->
                addOval(
                    Rect(
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = circularRadius.value
                    )
                )
            }
        }
}

/**
 * Draws a circular wave that expands from the touch point on each press. Purely visual;
 * does not consume touch — use on clickable or scrollable content.
 *
 * @param waveColor Color of the expanding circle. Default white with 0.4f alpha.
 * @param durationMillis Time for the circle to reach [radiusMax]. Default 600.
 * @param radiusMax Maximum radius of the wave in pixels. Default 150f.
 *
 * Usage:
 * ```
 * // Ripple-like feedback on a card
 * Card(
 *     modifier = Modifier
 *         .clickable { onClick() }
 *         .touchWaveEffect(waveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
 * ) { ... }
 *
 * // Full-screen overlay tap feedback
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .touchWaveEffect(radiusMax = 200f)
 *         .clickable { dismiss() }
 * )
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.touchWaveEffect(
    waveColor: Color = Color.White.copy(alpha = 0.4f),
    durationMillis: Int = 600,
    radiusMax: Float = 150f
): Modifier = composed {

    var size by remember { mutableStateOf(IntSize.Zero) }
    val radius = remember { Animatable(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    val scope = rememberCoroutineScope()

    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown()
                center = down.position

                scope.launch {
                    radius.snapTo(0f)
                    radius.animateTo(radiusMax, tween(durationMillis))
                    radius.snapTo(0f)
                }
            }
        }
    }
        .onGloballyPositioned { size = it.size }
        .drawWithContent {
            drawContent()
            drawCircle(
                color = waveColor,
                radius = radius.value,
                center = center
            )
        }
}

enum class FlipAxis { X, Y }

/**
 * Composable that shows [front] or [back] with a 3D flip animation. When [trigger] changes
 * (after first composition), the flip runs. Use for cards, toggles, or reveal answers.
 *
 * @param trigger When this value changes, a flip animation runs ([repeatCount] times).
 * @param modifier Applied to the flip container.
 * @param width Width of the flip area. Default 150.dp.
 * @param height Height of the flip area. Default 150.dp.
 * @param axis [FlipAxis.Y] for vertical flip (like a card), [FlipAxis.X] for horizontal.
 * @param repeatCount Number of 180° flips per trigger change. Default 1.
 * @param clockwise If true, rotation is clockwise. Default true.
 * @param durationMillis Duration of one 180° flip. Default 400.
 * @param front Content on the “front” (visible when rotation is near 0°).
 * @param back Content on the “back” (visible when rotation is near 180°).
 *
 * Usage:
 * ```
 * var flipped by remember { mutableStateOf(false) }
 * FlipSide(
 *     trigger = flipped,
 *     width = 200.dp,
 *     height = 120.dp,
 *     axis = FlipAxis.Y,
 *     front = { Text("Question") },
 *     back = { Text("Answer") }
 * )
 * Button(onClick = { flipped = !flipped }) { Text("Reveal") }
 *
 * // Horizontal flip (e.g. before/after)
 * FlipSide(trigger = showAfter, axis = FlipAxis.X, front = { Image(...) }, back = { Image(...) })
 * ```
 */
@Composable
fun FlipSide(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    height: Dp = 150.dp,
    axis: FlipAxis = FlipAxis.Y,
    repeatCount: Int = 1,
    clockwise: Boolean = true,
    durationMillis: Int = 400,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    var totalRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(0f) }
    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(trigger) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }
        val direction = if (clockwise) 1 else -1
        repeat(repeatCount) {
            val target = totalRotation + 180f * direction
            rotation.animateTo(target, tween(durationMillis))
            totalRotation = target
        }
    }

    Box(
        modifier = modifier
            .size(width, height)
            .graphicsLayer {
                if (axis == FlipAxis.Y) rotationY = rotation.value else rotationX = rotation.value
                cameraDistance = 12 * density
            }
    ) {
        if (rotation.value % 360f !in 90f..270f) {
            front()
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (axis == FlipAxis.Y) rotationY = 180f else rotationX = 180f
                    }
            ) {
                back()
            }
        }
    }
}

enum class ExpandDirection { VERTICAL, HORIZONTAL }

/**
 * Animates the composable’s height (or width) between 0 and full size based on [expanded].
 * Use for expandable sections, accordions, or dropdown content.
 *
 * @param expanded When true, content is fully visible; when false, size animates to zero.
 * @param durationMillis Duration of expand/collapse. Default 300.
 * @param direction [ExpandDirection.VERTICAL] animates height; [ExpandDirection.HORIZONTAL] animates width. Default VERTICAL.
 *
 * Usage:
 * ```
 * var expanded by remember { mutableStateOf(false) }
 * Column {
 *     Row(Modifier.clickable { expanded = !expanded }) {
 *         Text("Section title")
 *         Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
 *     }
 *     Column(Modifier.expandCollapse(expanded)) {
 *         Text("Hidden content that slides down when expanded.")
 *     }
 * }
 *
 * // Horizontal (e.g. side panel)
 * Row {
 *     Box(Modifier.expandCollapse(expanded = panelOpen, direction = ExpandDirection.HORIZONTAL)) {
 *         PanelContent()
 *     }
 * }
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.expandCollapse(
    expanded: Boolean,
    durationMillis: Int = 300,
    direction: ExpandDirection = ExpandDirection.VERTICAL
): Modifier = composed {

    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis)
    )

    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        val width = if (direction == ExpandDirection.HORIZONTAL)
            (placeable.width * progress).toInt() else placeable.width
        val height = if (direction == ExpandDirection.VERTICAL)
            (placeable.height * progress).toInt() else placeable.height

        layout(width, height) {
            placeable.place(0, 0)
        }
    }
}

enum class FloatingAxis { X, Y }

/**
 * Gently moves the composable back and forth (float / drift). Good for badges, icons, or
 * “attention” elements. By default runs infinitely; set [isInfinite] to false and [repeatCount]
 * for a limited number of cycles.
 *
 * @param trigger When this value changes, the effect (re)starts. Use [Unit] or a constant for infinite on first composition.
 * @param isInfinite If true, floats forever; if false, runs [repeatCount] times then stops at center. Default true.
 * @param offset Max displacement in pixels (each direction). Default 12f.
 * @param floatMillis Duration for one half-cycle (e.g. left-to-right). Default 1400.
 * @param repeatCount When [isInfinite] is false, number of full back-and-forth cycles. Ignored when [isInfinite] is true. Default 0.
 * @param axis [FloatingAxis.X] for horizontal drift, [FloatingAxis.Y] for vertical. Default X.
 *
 * Usage:
 * ```
 * // Subtle infinite float (e.g. FAB or notification badge)
 * Icon(
 *     imageVector = Icons.Default.Notifications,
 *     contentDescription = null,
 *     modifier = Modifier.floatingEffect(offset = 8f, floatMillis = 1200)
 * )
 *
 * // Vertical float for a “tap me” hint
 * Box(Modifier.floatingEffect(axis = FloatingAxis.Y, offset = 6f)) {
 *     Text("↓ Scroll")
 * }
 *
 * // One-time drift (e.g. 3 bounces then stop)
 * Modifier.floatingEffect(trigger = key, isInfinite = false, repeatCount = 3)
 * ```
 */
@Suppress("AvoidComposed")
fun Modifier.floatingEffect(
    trigger: Any? = Unit,
    isInfinite: Boolean = true,
    offset: Float = 12f,
    floatMillis: Int = 1400,
    repeatCount: Int = 0,
    axis: FloatingAxis = FloatingAxis.X,
): Modifier = composed {

    val anim = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (isInfinite) {
                while (true) {
                    anim.animateTo(
                        targetValue = offset,
                        animationSpec = tween(
                            durationMillis = floatMillis,
                            easing = LinearEasing
                        )
                    )
                    anim.animateTo(
                        targetValue = -offset,
                        animationSpec = tween(
                            durationMillis = floatMillis,
                            easing = LinearEasing
                        )
                    )
                }
        } else {
            for (i in 0 until repeatCount) {
                anim.animateTo(
                    targetValue = offset,
                    animationSpec = tween(
                        durationMillis = floatMillis,
                        easing = LinearEasing
                    )
                )
                anim.animateTo(
                    targetValue = -offset,
                    animationSpec = tween(
                        durationMillis = floatMillis,
                        easing = LinearEasing
                    )
                )
            }
            anim.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = floatMillis,
                    easing = LinearEasing
                )
            )
        }
    }

    graphicsLayer {
        if (axis == FloatingAxis.X) {
            translationX = anim.value
        } else {
            translationY = anim.value
        }
    }
}