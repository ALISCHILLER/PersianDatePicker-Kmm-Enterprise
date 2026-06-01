import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val hostOs: String = System.getProperty("os.name").lowercase()
val isMacOs: Boolean = hostOs.contains("mac")

// iOS / Apple targets need macOS + Xcode toolchain. On Windows they also force
// Gradle to resolve appleMain / iosMain configurations, which is exactly what
// caused the user's dependency-resolution failure. Keep them disabled on
// Windows/Linux unless explicitly requested.
val enableIosTargets: Boolean = providers.gradleProperty("enableIos")
    .map(String::toBoolean)
    .getOrElse(isMacOs)

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    js {
        outputModuleName = "persian-datepicker-sample"
        browser {
            commonWebpackConfig {
                outputFileName = "persian-datepicker-sample.js"
            }
        }
        binaries.executable()
    }

    wasmJs {
        outputModuleName = "persian-datepicker-sample"
        browser {
            commonWebpackConfig {
                outputFileName = "persian-datepicker-sample.js"
            }
        }
        binaries.executable()
    }

    if (enableIosTargets) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { target ->
            target.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":persian-datepicker"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(compose.preview)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "com.msa.persiandatepicker.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.msa.persiandatepicker.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.4.3"
    }
}

compose.desktop {
    application {
        mainClass = "com.msa.persiandatepicker.sample.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "PersianDatePickerKmmEnterpriseUltra"
            packageVersion = "2.4.3"
        }
    }
}
