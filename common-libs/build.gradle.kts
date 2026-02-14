plugins {
    id("java-library")
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // BOM Spring Boot
    val springBootBom = platform(libs.springBootDependencies)

    implementation(springBootBom)
    annotationProcessor(springBootBom)
    testImplementation(springBootBom)
    testAnnotationProcessor(springBootBom)

    // Public API (exported to consumers)
    api("jakarta.validation:jakarta.validation-api")

    // Compile-time only (do not leak to consumers)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

