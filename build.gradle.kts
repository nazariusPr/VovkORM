plugins {
    id("java-library")
    id("maven-publish")
    id("com.diffplug.spotless") version "7.0.0.BETA4"
}

group = "org.nazarius"
version = "1.0.0-BETA"
description = "VovkORM: lightweight Java ORM"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

val junitVersion = "5.10.0"
val mockitoVersion = "5.13.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "vovkorm"
        }
    }
    repositories {
        mavenLocal()
    }
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}