pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        val compose_ui_version = "1.5.0-alpha02"
        create("libs") {
            library(
                "koin.android",
                "io.insert-koin",
                "koin-android"
            ).version("3.3.3")
            library(
                "koin-androidx.compose",
                "io.insert-koin",
                "koin-androidx-compose"
            ).version("3.4.2")

            library(
                "androidx.core.core-ktx",
                "androidx.core",
                "core-ktx"
            ).version("1.9.0")

            library(
                "androidx.activity.activity-compose",
                "androidx.activity",
                "activity-compose"
            ).version("1.6.1")


            // androidx.compose
            library(
                "androidx.compose.ui-ui",
                "androidx.compose.ui",
                "ui"
            ).version(compose_ui_version)
            library(
                "androidx.compose.ui.ui-tooling-preview",
                "androidx.compose.ui",
                "ui-tooling-preview"
            ).version(compose_ui_version)
            library(
                "androidx.compose.ui.ui-util",
                "androidx.compose.ui",
                "ui-util"
            ).version(compose_ui_version)
            library(
                "androidx.compose.foundation-foundation",
                "androidx.compose.foundation",
                "foundation"
            ).version(compose_ui_version)
            library(
                "androidx.compose.foundation-foundation-layout",
                "androidx.compose.foundation",
                "foundation-layout"
            ).version(compose_ui_version)
            library(
                "androidx.compose.material.material",
                "androidx.compose.material",
                "material"
            ).version(compose_ui_version)
            library(
                "androidx.compose.material3.material3",
                "androidx.compose.material3",
                "material3"
            ).version("1.1.0")

            // KTOR
            library(
                "io.ktor.ktor.client.android",
                "io.ktor",
                "ktor-client-android"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.client.okhttp",
                "io.ktor",
                "ktor-client-okhttp"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.client.serialization",
                "io.ktor",
                "ktor-client-serialization"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.client-content-negotiation",
                "io.ktor",
                "ktor-client-content-negotiation",
            ).version("2.2.4")
            library(
                "io.ktor.ktor.serialization",
                "io.ktor",
                "ktor-serialization"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.serialization-kotlinx-json",
                "io.ktor",
                "ktor-serialization-kotlinx-json"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.client-auth",
                "io.ktor",
                "ktor-client-auth"
            ).version("2.2.4")
            library(
                "io.ktor.ktor.client-logging",
                "io.ktor",
                "ktor-client-logging"
            ).version("2.2.4")

            library(
                "io.coil-kt.coil",
                "io.coil-kt",
                "coil"
            ).version("2.3.0")
            library(
                "io.coil-kt.coil-compose",
                "io.coil-kt",
                "coil-compose"
            ).version("2.3.0")
            library(
                "io.coil.kt.coil-svg",
                "io.coil-kt",
                "coil-svg"
            ).version("2.3.0")

            library(
                "org.jetbrains.kotlinx.kotlinx-collections-immutable",
                "org.jetbrains.kotlinx",
                "kotlinx-collections-immutable"
            ).version("0.3.5")

            library(
                "org.jetbrains.kotlinx.atomicfu-jvm",
                "org.jetbrains.kotlinx",
                "atomicfu-jvm"
            ).version("0.20.2")

            library(
                "com.github.instacart.truetime-android",
                "com.github.instacart",
                "truetime-android"
            ).version("4.0.0.alpha")

            library(
                "org.jetbrains.kotlinx.kotlinx-datetime",
                "org.jetbrains.kotlinx",
                "kotlinx-datetime"
            ).version("0.4.0")
        }
    }
}
rootProject.name = "Valorant Companion"
include(":app")
include(":auth")
include(":base")
include(":career")
include(":live")
include(":assets")
include(":pvp")
include(":store")
