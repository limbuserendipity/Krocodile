plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.limbuserendipity.krocodile"
version = "1.0.0"
application {
    mainClass.set("com.limbuserendipity.krocodile.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation:2.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.0")
    implementation("io.ktor:ktor-server-websockets:2.2.0")
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}