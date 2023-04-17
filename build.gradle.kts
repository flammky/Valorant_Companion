buildscript {

    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.19.0")
    }
    repositories {
        mavenCentral()
        google()
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("com.google.devtools.ksp") version "1.8.0-1.0.8" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
}

apply(plugin = "kotlinx-atomicfu")