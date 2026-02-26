# Sqringy - Quick Animation Extensions

Beautiful, reusable micro-animation extensions for Jetpack Compose.

This repository contains:

- **quick-animation-ext** — main animation library (Modifier extensions + composables)
- **app** — sample app demonstrating all effects

[![](https://jitpack.io/v/meetOzan/Springy.svg)](https://jitpack.io/#meetOzan/Springy)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)


---

## 1. Installation

### 1.1 Repository

Add JitPack (e.g. in `settings.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### 1.2 Dependency

**Kotlin DSL:**

```kotlin
dependencies {
    implementation(platform("androidx.compose:compose-bom:<version>"))
    implementation("com.github.meetOzan:quick-animation-ext:<release-version>")
}
```

**Groovy:**

```groovy
implementation platform("androidx.compose:compose-bom:<version>")
implementation "com.github.meetOzan:quick-animation-ext:<release-version>"
```

Replace `<version>` with your Compose BOM version and `<release-version>` with the library version (e.g. `0.0.1`).

---

## 2. API overview

All APIs live in package `com.meetozan.quick_animation_ext`.

| Category | API | Description |
|----------|-----|-------------|
| Press feedback | `Modifier.clickBounceEffect` | Scale bounce on press (for buttons / clickable) |
| Shake | `Modifier.shakeHorizontal` | Horizontal shake (e.g. form error) |
| Shake | `Modifier.shakeVertical` | Vertical shake |
| Scale | `Modifier.scaleUpDown` | One-off scale up/down (e.g. star, badge) |
| Scale | `Modifier.heartBeatEffect` | Heartbeat-style scale (e.g. like button) |
| Rotation | `Modifier.wiggle` | Small rotation wiggle (CTA attention) |
| Rotation | `Modifier.spin` | Full rotation (refresh, loading) |
| Enter/exit | `SlideInRightFadeIn` | Content slides in from right + fade |
| Enter/exit | `SlideInLeftFadeIn` | Content slides in from left + fade |
| Loading | `Modifier.shimmerEffect` | Skeleton shimmer (fade out with `endTrigger`) |
| Scroll | `Modifier.elasticOverscroll` | Rubber-band overscroll (e.g. pull-to-refresh) |
| Scroll | `Modifier.parallax` | Parallax offset from scroll position |
| Reveal | `Modifier.circularReveal` | Circular clip expand/collapse |
| Touch | `Modifier.touchWaveEffect` | Expanding circle from touch point |
| Flip | `FlipSide` | 3D flip composable (front/back) |
| Layout | `Modifier.expandCollapse` | Animated height/width expand/collapse |
| Ambient | `Modifier.floatingEffect` | Gentle back-and-forth float |

---

## 3. Demo GIFs

---

### clickBounceEffect  

![clickBounceVideo (1)](https://github.com/user-attachments/assets/e15be850-baa5-4662-9a34-27f0be91d425)

---

### shakeHorizontal  

![Shake Horizontal Video](https://github.com/user-attachments/assets/6a9b1c9e-6a39-44c6-a3f3-c96645c2a954)


---

### shakeVertical  

![shakeVertical](https://github.com/user-attachments/assets/e65f3b13-9f03-4e19-917e-b15be3294347)


---

### scaleUpDown  

![scaleUpDown](https://github.com/user-attachments/assets/f48d1fa8-ac83-429f-8a73-d1b58cb15bfd)


---

### heartBeatEffect  

![heartBeatEffect](https://github.com/user-attachments/assets/8434db9e-1713-4218-921f-2c1a7d116062)

---

### wiggle  

![wiggle](https://github.com/user-attachments/assets/0aa7867d-c780-44e2-b9ba-69bae6c6eda2)


---

### spin  

![spin](https://github.com/user-attachments/assets/61943fa1-ce0d-4298-a61e-28859cc638c4)


---

### SlideInLeftFadeIn & SlideInRightFadeIn  

![SlideInFadeIn](https://github.com/user-attachments/assets/2a3b90f7-50cf-4f81-821c-8c1018af7f6d)


---

### shimmerEffect  

![shimmerEffect](https://github.com/user-attachments/assets/97cd928a-b5ce-454a-bfbc-de999c611dc2)


---

### elasticOverscroll  

![elasticOverscroll](https://github.com/user-attachments/assets/33fad773-5a25-4572-a705-8f3571c7f652)


---

### parallax

![parallax](https://github.com/user-attachments/assets/d519f1be-0e27-4937-8322-567aec662af6)


---

### circularReveal  

![circularReveal](https://github.com/user-attachments/assets/4e2a8d68-156e-4959-9219-d6c66c44c71f)


---

### touchWaveEffect  

![touchWaveEffect](https://github.com/user-attachments/assets/d63abad2-3e24-442d-b146-33f297f499fc)


---

### FlipSide  

![FlipSide](https://github.com/user-attachments/assets/61d08e89-8d7f-42e6-9d84-686a4559d87a)


---

### expandCollapse  

![expandCollapse](https://github.com/user-attachments/assets/2b2c7a70-51ab-467c-a286-59d0829a5e78)


---

### floatingEffect  

![floatingEffect](https://github.com/user-attachments/assets/6676b829-32c1-42a8-ae66-6ebb3688559b)


---

## 4. Quick usage examples

### clickBounceEffect (recommended on clickable components)

```kotlin
Button(modifier = Modifier.clickBounceEffect(), onClick = { ... }) { Text("Submit") }
IconButton(modifier = Modifier.clickBounceEffect(scaleDown = 0.92f), onClick = { ... }) { ... }
```

### shakeHorizontal / shakeVertical (error feedback)

```kotlin
OutlinedTextField(
    modifier = Modifier.shakeHorizontal(trigger = showError, onAnimationEnd = { showError = false }),
    ...
)
Card(modifier = Modifier.shakeVertical(trigger = denyKey)) { ... }
```

### scaleUpDown / heartBeatEffect (pulse, like)

```kotlin
Icon(..., modifier = Modifier.scaleUpDown(trigger = liked, scaleUp = 1.3f))
Icon(..., modifier = Modifier.heartBeatEffect(trigger = loved))
```

### wiggle / spin

```kotlin
Button(modifier = Modifier.wiggle(enabled = wiggle), ...)
Icon(..., modifier = Modifier.spin(trigger = refreshKey, durationMillis = 500))
Icon(..., modifier = Modifier.spin(trigger = Unit, isInfinite = true))
```

### SlideInRightFadeIn / SlideInLeftFadeIn

```kotlin
SlideInRightFadeIn(durationMillis = 400, delayMillis = 100) { Text("Welcome") }
SlideInLeftFadeIn(trigger = showDrawer, isHaveExitAnimation = true) { DrawerContent() }
```

### shimmerEffect (skeleton)

```kotlin
Box(Modifier.fillMaxWidth().height(120.dp).shimmerEffect(endTrigger = loaded))
```

### elasticOverscroll / parallax

```kotlin
Column(Modifier.verticalScroll(state).elasticOverscroll(overscrollAction = { refresh() })) { ... }
Image(Modifier.parallax(scrollState.value, ratio = 0.4f), ...)
```

### circularReveal / touchWaveEffect

```kotlin
Box(Modifier.circularReveal(trigger = show, isClose = !show)) { FullScreenContent() }
Card(Modifier.clickable { }.touchWaveEffect()) { ... }
```

### FlipSide / expandCollapse / floatingEffect

```kotlin
FlipSide(trigger = flipped, front = { Text("Q") }, back = { Text("A") })
Column(Modifier.expandCollapse(expanded)) { ... }
Icon(Modifier.floatingEffect(offset = 8f), ...)
```

---

## 5. Design goals

- **Composable-first** — Modifier extensions and small composables; no heavy wrappers.
- **Sane defaults** — Most parameters have defaults; override when needed.
- **Extensible** — Shared internals (e.g. baseShake, baseHeartBeat) allow new variants.

---

## 6. License

QuickAnimationExt is licensed under the **Apache License, Version 2.0**.

```
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
 
