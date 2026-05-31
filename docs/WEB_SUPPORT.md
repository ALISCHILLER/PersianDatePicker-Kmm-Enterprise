# Web Support

The sample app supports both browser targets used by Compose Multiplatform:

- `js` for Kotlin/JS browser builds.
- `wasmJs` for Kotlin/Wasm browser builds.

Both entry points live in the sample app:

```text
sampleApp/src/jsMain/kotlin/com/zargroup/persiandatepicker/sample/Main.kt
sampleApp/src/wasmJsMain/kotlin/com/zargroup/persiandatepicker/sample/Main.kt
```

Both entry points launch the same shared UI and bind it to an explicit host element:

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "webApp") {
        SampleApp()
    }
}
```

The HTML files must provide the matching container and explicit full-screen sizing:

```html
<div id="webApp"></div>
```

```css
html, body, #webApp {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
    overflow: hidden;
}
```

## Run web locally

```bash
./gradlew :sampleApp:wasmJsBrowserDevelopmentRun
./gradlew :sampleApp:jsBrowserDevelopmentRun
```

## Build web artifacts

```bash
./gradlew :sampleApp:wasmJsBrowserDistribution
./gradlew :sampleApp:jsBrowserDistribution
```

## Compatibility distribution

When both modern Wasm-capable browsers and older JS fallback scenarios matter, build the compatibility package:

```bash
./gradlew :sampleApp:composeCompatibilityBrowserDistribution
```

## Web design notes

- The browser `index.html` is included under both `jsMain/resources` and `wasmJsMain/resources`.
- The picker UI remains in `commonMain`; web source sets only launch the UI.
- Keep all business logic out of web-specific source sets.
- Avoid using Android-only APIs, JVM-only APIs, `java.time`, or platform resources in `persian-datepicker/src/commonMain`.
- If a future feature requires DOM APIs, isolate it behind a web-specific adapter rather than leaking it into the library core.
