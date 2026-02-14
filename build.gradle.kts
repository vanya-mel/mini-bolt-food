plugins {
    id("java-base")
}

allprojects {
    group = "cz.dev.vanya.miniboltfood"

    repositories {
        mavenCentral()
    }
}


subprojects {
    // Compile/test all Java subprojects with Java 21 toolchain.
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}