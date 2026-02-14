pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.springframework.boot") version "4.0.2"
        id("io.spring.dependency-management") version "1.1.7"
    }
}

rootProject.name = "mini-bolt-food"

include(
    "common-libs",
    "delivery-service",
    "order-service",
    "payment-service"
)