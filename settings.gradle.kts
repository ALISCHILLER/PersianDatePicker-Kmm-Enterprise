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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Keep after official repositories so official metadata wins.
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))
        maven(url = uri("https://redirector.kotlinlang.org/maven/compose-dev"))
        maven(url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev"))
        maven(url = uri("https://packages.jetbrains.team/maven/p/cmp/dev"))
    }
}

rootProject.name = "PersianDatePickerKmmEnterpriseUltraWindowsSafe"
include(":persian-datepicker")
include(":sampleApp")
