plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
//    implementation(libs.slf4j.api)
}
