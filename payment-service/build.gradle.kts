plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // Internal modules
    implementation(project(":common-libs"))

    // Spring starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Additional libs
    implementation(libs.mapstruct)
    implementation(libs.springdocWebmvcUi)

    // Database (runtime only)
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // Compile-time annotation processors
    annotationProcessor(libs.mapstructProcessor)
    annotationProcessor(libs.lombokMapstructBinding)
    annotationProcessor("org.projectlombok:lombok")

    // Lombok (compile only)
    compileOnly("org.projectlombok:lombok")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

