plugins {
    kotlin("jvm") version "1.8.0"
    id("me.champeau.jmh") version "0.6.5"
    kotlin("kapt") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.openjdk.jmh:jmh-core:1.35")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.35")
}

kotlin {
    jvmToolchain(11)
}