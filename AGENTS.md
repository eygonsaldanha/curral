# AGENTS.md

## Project Overview
Curral is a **Kotlin Multiplatform** farm management app (Android, iOS, Desktop/JVM, Server).  
Package: `ey.buriti.curral` — all modules share this root namespace.

## Module Map
| Module | Role | Entry Point |
|---|---|---|
| `composeApp/` | Compose Multiplatform UI (Android + iOS + Desktop) | `App.kt` → `Scaffold` with bottom nav |
| `server/` | Ktor HTTP API (Netty, port from `shared/.../Constants.kt`) | `Application.kt` → `embeddedServer` |
| `shared/` | KMP library shared by all targets (models, constants, `expect/actual`) | consumed via `projects.shared` |
| `iosApp/` | Thin SwiftUI shell embedding `ComposeApp` framework | `ContentView.swift` → `MainViewControllerKt.MainViewController()` |

## Architecture Conventions
- **UI lives in `composeApp/src/commonMain/.../ui/`**: screens in `ui/screens/`, reusable components in `ui/components/`, colors in `ui/theme/CurralColors.kt`.
- **Navigation**: state-based (`Screen` enum in `navigation/Screen.kt`); screen routing in `App.kt` `when` block. No external nav library.
- **Bottom bar**: custom `CurralBottomBar` with 4 tabs + center FAB (`ui/components/CurralBottomBar.kt`).
- **Platform abstractions** use `expect/actual` in `shared/` (see `Platform.kt` + `Platform.{android,ios,jvm}.kt`).
- **Shared constants** (e.g. `SERVER_PORT`) go in `shared/.../Constants.kt` so both `server` and `composeApp` reference them.
- **Icons**: `compose.materialIconsExtended` (pinned 1.7.3); prefer `Icons.AutoMirrored.Filled.*` for directional icons.

## Build & Run (Windows — use `./gradlew` on macOS/Linux)
```
.\gradlew.bat :composeApp:run            # Desktop (JVM)
.\gradlew.bat :composeApp:assembleDebug  # Android APK
.\gradlew.bat :server:run                # Ktor server (localhost:8080)
.\gradlew.bat :server:test               # Server tests (Ktor testApplication)
.\gradlew.bat :shared:allTests           # Shared multiplatform tests
```

## Key Tech & Versions (see `gradle/libs.versions.toml`)
- Kotlin **2.3.20**, Compose Multiplatform **1.10.3**, Ktor **3.4.1**
- Compose Hot Reload plugin enabled (`composeHotReload`)
- Gradle **configuration-cache** and **build caching** are ON
- Version catalog (`libs.versions.toml`) is the single source of truth for dependency versions

## Code Style
- `kotlin.code.style=official` — follow Kotlin coding conventions
- JVM target: **11** for both Android and shared
- Material3 + `materialIconsExtended` for UI icons
- Server tests use `testApplication { }` with Ktor test host (see `ApplicationTest.kt`)
- Portuguese UI strings (labels, placeholders, alerts) — keep consistent language

## When Adding Features
1. **New screen**: add `Screen` enum entry in `navigation/Screen.kt`, create composable in `ui/screens/`, wire in `App.kt` `when` block.
2. **New UI component**: place in `ui/components/`; use colors from `CurralColors` object, not hardcoded hex values.
3. **New API endpoint**: add route in `server/.../Application.kt` `routing { }`, add matching test in `ApplicationTest.kt`.
4. **Shared model/logic**: place in `shared/src/commonMain/`; if platform-specific, use `expect`/`actual` pattern.
5. **New dependency**: add version + library entry in `gradle/libs.versions.toml`, reference via `libs.*` in `build.gradle.kts`.
