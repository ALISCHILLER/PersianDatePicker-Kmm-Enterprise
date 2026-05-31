import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":persian-datepicker"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(compose.preview)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "com.zargroup.persiandatepicker.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zargroup.persiandatepicker.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.4.0"
    }
}

compose.desktop {
    application {
        mainClass = "com.zargroup.persiandatepicker.sample.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "PersianDatePickerKmmEnterpriseUltra"
            packageVersion = "2.4.0"
        }
    }
}
