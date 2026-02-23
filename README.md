## QuickAnimationExt

Beautiful, reusable micro‑animations for Jetpack Compose – with optional lint rules to keep your UI interactions consistent and accessible.

This repository contains:
- `quick-animation-ext`: the main animation extension library (published as a Maven artifact)
- `quick-animations-lint`: optional Android Lint checks for safer usage of some APIs (especially `clickBounceEffect`)
- `app`: sample app demonstrating the effects

---

### 1. Installation

#### 1.1. Repository

QuickAnimationExt is published via **JitPack** under:

```kotlin
// settings.gradle / dependencyResolutionManagement
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

#### 1.2. Gradle dependency (Kotlin DSL)

```kotlin
dependencies {
    // Jetpack Compose BOM (recommended)
    implementation(platform("androidx.compose:compose-bom:<version>"))

    // QuickAnimationExt
    implementation("com.github.meetOzan:quick-animation-ext:0.0.1")
}
```

Groovy DSL:

```groovy
dependencies {
    implementation platform("androidx.compose:compose-bom:<version>")
    implementation "com.github.meetOzan:quick-animation-ext:0.0.1"
}
```

> **Note**: Replace `<version>` with the Compose BOM version you use in your project.

---

### 2. Overview of Animations

All runtime APIs live under the `com.meetozan.quick_animation_ext` package and are designed to:
- be easy to drop into existing composables,
- keep parameters small & meaningful,
- play nicely with state‑driven Compose UIs.

#### 2.1. `Modifier.clickBounceEffect`

Adds a subtle scale “bounce” when the composable is pressed.

```kotlin
Button(
    modifier = Modifier.clickBounceEffect(),
    onClick = { /* Handle click */ }
) {
    Text("Click Me")
}
```

Key parameter:
- `scaleDown: Float = 0.90f` – scale when pressed.

> **Lint support**: there is an optional lint rule that warns if `clickBounceEffect` is applied on non‑interactive content (see **Section 4**).

---

#### 2.2. Shake animations

##### 2.2.1. `Modifier.shakeHorizontal`

Triggers a horizontal shake animation, usually for **error / invalid input feedback**.

```kotlin
var shakeTrigger by remember { mutableStateOf(false) }

Box(
    modifier = Modifier
        .shakeHorizontal(trigger = shakeTrigger)
) {
    Text("Invalid value")
}

Button(onClick = { shakeTrigger = !shakeTrigger }) {
    Text("Trigger shake")
}
```

Parameters:
- `trigger: Any?` – when this value changes, the animation runs.
- `durationMillis: Int = 500`
- `intensity: Float = 20f`
- `onAnimationEnd: (() -> Unit)? = null`

##### 2.2.2. `Modifier.shakeVertical`

Same as horizontal shake but on the Y axis:

```kotlin
Box(
    modifier = Modifier.shakeVertical(trigger = formErrorKey)
) { /* ... */ }
```

Both `shakeHorizontal` and `shakeVertical` share the same parameter set and forward to a common internal implementation.

---

#### 2.3. Heartbeat & scale effects

##### 2.3.1. `Modifier.scaleUpDown`

A simple scale‑in / scale‑out animation. Good for attention‑grabbing micro interactions.

```kotlin
var pulse by remember { mutableStateOf(false) }

Icon(
    imageVector = Icons.Default.Star,
    contentDescription = null,
    modifier = Modifier.scaleUpDown(
        trigger = pulse,
        durationMillis = 600,
        scaleUp = 1.2f,
        scaleDown = 1f
    )
)

LaunchedEffect(Unit) {
    pulse = !pulse
}
```

##### 2.3.2. `Modifier.heartBeatEffect`

A more “organic” heartbeat animation with easing tuned to feel like a real pulse.

```kotlin
var loved by remember { mutableStateOf(false) }

Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = null,
    modifier = Modifier.heartBeatEffect(
        trigger = loved,
        isInfinite = false
    )
)
```

Parameters (shared with `scaleUpDown`):
- `trigger: Any? = null`
- `durationMillis: Int = 600`
- `scaleUp: Float = 1.20f`
- `scaleDown: Float = 1f`
- `isInfinite: Boolean = false`
- `onAnimationEnd: (() -> Unit)? = null`

---

#### 2.4. `Modifier.wiggle`

Small rotation wiggle, typically used to draw attention to a control.

```kotlin
var wiggle by remember { mutableStateOf(false) }

Button(
    onClick = { wiggle = true },
    modifier = Modifier.wiggle(
        enabled = wiggle,
        durationMillis = 300,
        rotationAngle = 5f
    )
) {
    Text("Wiggle me")
}
```

Parameters:
- `enabled: Boolean`
- `durationMillis: Int = 300`
- `rotationAngle: Float = 5f`

---

#### 2.5. `Modifier.spin`

Rotates the composable around the Z axis.

```kotlin
var spinTrigger by remember { mutableStateOf(false) }

Icon(
    imageVector = Icons.Default.Refresh,
    contentDescription = null,
    modifier = Modifier.spin(
        trigger = spinTrigger,
        durationMillis = 1000,
        repeatCount = 3,
        rotationAngle = 360f,
        side = SpinSide.CLOCKWISE
    )
)

Button(onClick = { spinTrigger = !spinTrigger }) {
    Text("Spin")
}
```

Parameters:
- `trigger: Any? = null`
- `durationMillis: Int = 1000`
- `repeatCount: Int = 1`
- `rotationAngle: Float = 360f`
- `isInfinite: Boolean = false`
- `side: SpinSide = SpinSide.CLOCKWISE`

---

#### 2.6. Slide + fade composables

##### 2.6.1. `SlideInRightFadeIn`

```kotlin
SlideInRightFadeIn(
    durationMillis = 300,
    delayMillis = 0,
    isHaveExitAnimation = true
) {
    Button(onClick = { /* ... */ }) {
        Text("Slide from right")
    }
}
```

Parameters:
- `modifier: Modifier = Modifier`
- `trigger: Any? = Unit` – when this changes, the enter animation runs again
- `durationMillis: Int = 300`
- `delayMillis: Int = 0`
- `easing: Easing = LinearOutSlowInEasing`
- `contentAlign: Alignment = Alignment.Center`
- `isHaveExitAnimation: Boolean = false`

##### 2.6.2. `SlideInLeftFadeIn`

Same API, but content enters from the left:

```kotlin
SlideInLeftFadeIn(
    durationMillis = 300,
    isHaveExitAnimation = true
) {
    Text("Hello from the left")
}
```

---

#### 2.7. `Modifier.shimmerEffect`

Adds a shimmering skeleton‑loading effect over the composable.

```kotlin
var isShimmerFinished by remember { mutableStateOf(false) }

Box(
    modifier = Modifier
        .size(120.dp)
        .shimmerEffect(endTrigger = isShimmerFinished)
)

// Once data is loaded
LaunchedEffect(Unit) {
    delay(1500)
    isShimmerFinished = true
}
```

Parameters:
- `endTrigger: Boolean` – when true, shimmer fades out and stops.
- `shimmerColor: Color`
- `baseColor: Color`
- `durationMillis: Int = 1000`
- `shape: Shape = RoundedCornerShape(4)`

---

### 3. Lint integration (optional but recommended)

Module: `quick-animations-lint`  
Package: `com.meetozan.quick_animations_lint`

This module adds a custom lint rule that focuses on **safe use of `clickBounceEffect`**.

#### 3.1. What it checks

Issue ID: `NonInteractiveContentWarning`

The rule is triggered when:
- `Modifier.clickBounceEffect()` is used
- **and** the modifier is applied to something that does **not** appear to be:
  - a `Button` / `IconButton` (and Material3 button family), or
  - a composable with a `.clickable()` / `.combinedClickable()` modifier in its chain.

In those cases, the detector reports a warning like:

> `Usage warning! 'clickBounceEffect' should only be used on clickable elements (Button, IconButton, etc.) or elements with .clickable() modifier.`

This prevents accidentally giving “click” affordance to non‑interactive content (e.g. `TextField`, `Box` without click handling, etc.).

#### 3.2. Enabling the lint module (monorepo / local use)

If you are consuming this repository as modules in your project, add:

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":quick-animation-ext"))

    // Lint checks (app module only)
    lintChecks(project(":quick-animations-lint"))
}
```

> **Note:** For released artifacts, the lint module can be shipped as a separate dependency (e.g. `lintChecks("com.github.meetOzan:quick-animations-lint:<version>")`). The structure in this repo is already prepared for that.

#### 3.3. Suppressing the lint rule – `@SpringySuppress`

Sometimes you **intentionally** want to put `clickBounceEffect` on a non‑standard component. For those cases, the lint module exposes:

```kotlin
package com.meetozan.quick_animations_lint

@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE
)
annotation class SpringySuppress(val reason: String = "")
```

Usage example:

```kotlin
import com.meetozan.quick_animations_lint.SpringySuppress

class MainActivity : ComponentActivity() {

    @SpringySuppress("Intentionally using bounce on TextField")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TextField(
                modifier = Modifier.clickBounceEffect(),
                value = state,
                onValueChange = { state = it }
            )
        }
    }
}
```

The detector looks for `@SpringySuppress`:
- on the method,
- on the surrounding class,
- or on the specific expression / node,
and **skips reporting** the issue when it is present.

> The annotation’s `reason` parameter is optional (`""` by default) so you can decide whether you want to document the reason or keep it minimal.

---

### 4. Demo videos (to be added by you)

You mentioned you will record videos for the animations. Here are suggested placeholders you can fill later with GIFs / MP4 links.

- **Button bounce & click feedback**
  - _Video placeholder_: `<!-- TODO: embed bounce effect video -->`
- **Shake animations for invalid input**
  - _Video placeholder_: `<!-- TODO: embed shakeHorizontal / shakeVertical video -->`
- **Heartbeat & scale animations**
  - _Video placeholder_: `<!-- TODO: embed heartBeatEffect / scaleUpDown video -->`
- **Wiggle & spin for call‑to‑action**
  - _Video placeholder_: `<!-- TODO: embed wiggle + spin video -->`
- **Slide + shimmer for skeleton loading**
  - _Video placeholder_: `<!-- TODO: embed SlideInLeft/Right + shimmerEffect video -->`

Once you have the assets, you can embed them like:

```markdown
![Bounce demo](https://your.cdn/bounce-demo.gif)
```

or (for HTML5 video):

```html
<video src="https://your.cdn/bounce-demo.mp4" autoplay loop muted playsinline></video>
```

---

### 5. Design goals & best practices

- **Composable‑first**  
  APIs are designed to feel natural in Compose: everything is either a `Modifier` extension or a small composable wrapper.

- **Minimal configuration, sane defaults**  
  Most functions have defaults that should feel good for the majority of use‑cases. You can override them when you need more control.

- **Safe defaults via lint**  
  The lint module is intentionally narrow in scope: it focuses only on `clickBounceEffect`, where mis‑use can hurt UX.

- **Extensible**  
  Internally shared helpers like `baseShake` and `baseHeartBeat` make it easy to add new variants or expose more fine‑grained APIs later.

---

### 6. License

QuickAnimationExt is licensed under the **Apache License, Version 2.0**.

```text
Copyright 2026 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

