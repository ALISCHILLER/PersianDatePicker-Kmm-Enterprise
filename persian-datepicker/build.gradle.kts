import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    `maven-publish`
    `signing`
}

val hostOs: String = System.getProperty("os.name").lowercase()
val isMacOs: Boolean = hostOs.contains("mac")

// Keep Apple targets opt-in on non-macOS hosts. This avoids resolving appleMain /
// iosMain configurations on Windows while preserving full iOS support on macOS.
val enableIosTargets: Boolean = providers.gradleProperty("enableIos")
    .map(String::toBoolean)
    .getOrElse(isMacOs)

kotlin {
    explicitApi()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }

    jvm("desktop")

    js {
        browser()
    }

    wasmJs {
        browser()
    }

    if (enableIosTargets) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { target ->
            target.binaries.framework {
                baseName = "PersianDatePicker"
                isStatic = true
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)

            api(compose.runtime)
            api(compose.runtimeSaveable)
            api(compose.foundation)
            api(compose.animation)
            api(compose.material3)
            api(compose.ui)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.msa.persiandatepicker"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named("dokkaGenerate"))
    archiveClassifier.set("javadoc")
    from(layout.buildDirectory.dir("dokka/html"))
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(dokkaHtmlJar)
        pom {
            name.set("Persian DatePicker KMM")
            description.set("A publish-ready Persian/Jalali DatePicker library for Compose Multiplatform on Android, iOS, Desktop, JS, and Wasm.")
            url.set("https://github.com/msa/persian-datepicker-kmm")
            licenses {
                license {
                    name.set("GNU General Public License, Version 3.0")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("msa")
                    name.set("MSA")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/msa/persian-datepicker-kmm.git")
                developerConnection.set("scm:git:ssh://git@github.com/msa/persian-datepicker-kmm.git")
                url.set("https://github.com/msa/persian-datepicker-kmm")
            }
        }
    }

    repositories {
        maven {
            name = "localStaging"
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingInMemoryKey").orNull
    val signingPassword = providers.gradleProperty("signingInMemoryKeyPassword").orNull
    val signingRequired = providers.gradleProperty("releaseSigningRequired")
        .map(String::toBoolean)
        .getOrElse(false)

    isRequired = signingRequired
    if (signingRequired && (signingKey.isNullOrBlank() || signingPassword.isNullOrBlank())) {
        throw org.gradle.api.GradleException(
            "releaseSigningRequired=true but signingInMemoryKey/signingInMemoryKeyPassword were not provided.",
        )
    }
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
