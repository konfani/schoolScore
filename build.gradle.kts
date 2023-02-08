val ktorVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    implementation("com.google.guava:guava:31.1-jre")
    
    implementation("com.github.twitch4j:twitch4j:1.13.0")
    implementation("com.github.philippheuer.events4j:events4j-handler-reactor:0.11.0")
    
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    
    implementation("kg.net.bazi.gsb4j:gsb4j-core:1.0.6")
    
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    
    implementation("io.kubernetes:client-java:15.0.1")
    
}
application {
    mainClass.set("MainKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
}
/*tasks.jar {
    manifest.attributes["Main-Class"] = "Mainkt"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}*/