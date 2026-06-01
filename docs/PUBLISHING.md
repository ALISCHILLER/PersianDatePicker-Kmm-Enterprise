# Publishing guide

The library module is `:persian-datepicker` and is configured with `maven-publish`.

## Local staging publication

### Host requirements

Kotlin Multiplatform creates separate publications per target. Run full release publication on macOS when Apple/iOS artifacts are part of the release matrix. Linux CI is still useful for common, Android, Desktop, JS, Wasm, and Dokka checks.


```bash
./gradlew :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

Artifacts are written to:

```text
persian-datepicker/build/staging-deploy
```

## Publication coordinates

```text
groupId:    com.msa.persiandatepicker
artifactId: persian-datepicker
version:    2.4.0
```

Kotlin Multiplatform creates platform-specific publications for metadata, Android, Desktop JVM, iOS, JS, and Wasm targets.

## Release gate

Run these before publishing a release tag:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :sampleApp:assembleDebug \
  :sampleApp:wasmJsBrowserDistribution \
  :sampleApp:jsBrowserDistribution \
  :sampleApp:composeCompatibilityBrowserDistribution
```

On macOS, also run:

```bash
./gradlew \
  :persian-datepicker:compileKotlinIosSimulatorArm64 \
  :sampleApp:compileKotlinIosSimulatorArm64
```


## API documentation

Dokka is wired into the library module. Generate API docs with:

```bash
./gradlew :persian-datepicker:dokkaGenerate
```

## Optional signing

For signed releases, provide secrets through CI or local Gradle properties:

```properties
signingInMemoryKey=<ascii-armored-private-key>
signingInMemoryKeyPassword=<key-password>
releaseSigningRequired=true
```

Signing is intentionally optional for local development and staging publication.

## Maven Central notes

For Maven Central/Sonatype, add a remote repository and signing configuration in a private Gradle init script or CI secret-backed block. Do not commit private credentials or signing keys.

The current POM license metadata intentionally matches the repository `LICENSE` file: GNU GPL v3. Review `docs/LICENSE_POLICY.md` before publishing.

## Enterprise release publishing notes

The library module uses strict explicit API mode and attaches a Dokka HTML JAR to Maven publications. Before publishing remotely, run:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:dokkaGenerate \
  :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

For a full Apple target matrix, run the publishing command on macOS so iOS publications can be produced and validated consistently.
