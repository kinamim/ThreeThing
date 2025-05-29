plugins {
    id("com.android.application") version "8.10.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.protobuf") version "0.9.4" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}