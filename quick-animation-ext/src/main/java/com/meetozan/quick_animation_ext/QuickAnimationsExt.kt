package com.meetozan.quick_animation_ext

/*
   Created by meetOzan on 04.02.2026
*/

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput


/**
 * A modifier that adds a bounce effect when the composable is clicked.
 *
 * @param scaleDown The scale factor to apply when the composable is pressed. Default is 0.90f (90% of original size).
 *
 * Usage:
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

                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

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