import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-math3:3.6.1")

    testImplementation("org.testng:testng:7.3.0") {
        exclude("junit", "junit")
    }
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

