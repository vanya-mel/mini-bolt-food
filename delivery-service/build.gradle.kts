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
//    implementation("org.springframework.kafka:spring-kafka")
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    runtimeOnly("org.postgresql:postgresql")

    // Additional libs
    implementation(libs.mapstruct)
    implementation(libs.springdocWebmvcUi)

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

