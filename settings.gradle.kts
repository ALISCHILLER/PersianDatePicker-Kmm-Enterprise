pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // Stable Compose Multiplatform artifacts are published to Maven Central.
        // These fallbacks are useful for RC/dev artifacts or restricted networks.
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))
        maven(url = uri("https://redirector.kotlinlang.org/maven/compose-dev"))
        maven(url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev"))
        maven(url = uri("https://packages.jetbrains.team/maven/p/cmp/dev"))
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // Keep after official repositories so official metadata wins.
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))
        maven(url = uri("https://redirector.kotlinlang.org/maven/compose-dev"))
        maven(url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev"))
        maven(url = uri("https://packages.jetbrains.team/maven/p/cmp/dev"))

        // Required by Kotlin/JS and Wasm targets.
        // Kotlin downloads Node.js distributions from nodejs.org, not Maven Central.
        ivy("https://nodejs.org/dist/") {
            name = "Node.js Distributions"

            patternLayout {
                artifact("v[revision]/[artifact]-v[revision]-[classifier].[ext]")
            }

            metadataSources {
                artifact()
            }

            content {
                includeModule("org.nodejs", "node")
            }
        }

        ivy("https://github.com/yarnpkg/yarn/releases/download/") {
            name = "Yarn Distributions"

            patternLayout {
                artifact("v[revision]/[artifact]-v[revision].[ext]")
            }

            metadataSources {
                artifact()
            }

            content {
                includeModule("com.yarnpkg", "yarn")
            }
        }
    }
}

rootProject.name = "PersianDatePickerKmmEnterpriseUltraWindowsSafe"

include(":persian-datepicker")
include(":sampleApp")
