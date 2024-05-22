plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation("io.ktor:ktor-server-netty:2.3.11")
    implementation("io.ktor:ktor-server-websockets:2.3.11")
    implementation("ch.qos.logback:logback-classic:1.2.11") // Logbackの依存関係
    implementation("org.slf4j:slf4j-api:1.7.32")
}
