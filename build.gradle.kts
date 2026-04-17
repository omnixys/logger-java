plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "4.0.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

group = findProperty("group") as String
version = findProperty("version") as String
description = "Omnixys logger Spring Integration Package"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    // mavenLocal()
    mavenCentral()

    maven {
        name = "GitHubKafka"
        url = uri("https://maven.pkg.github.com/omnixys/kafka-java")

        credentials {
            username = System.getenv("GITHUB_ACTOR")
                ?: findProperty("gpr.user") as String?
                        ?: ""
            password = System.getenv("GITHUB_TOKEN")
                ?: findProperty("gpr.key") as String?
                        ?: ""
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.4")
        mavenBom("io.opentelemetry:opentelemetry-bom:1.55.0")
    }
}

dependencies {
    api("com.omnixys:kafka:1.0.0")

    // Jackson for structured logs
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Logging
    implementation("org.slf4j:slf4j-api")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * 🔥 Ensure version is visible in build logs
 */
tasks.register("printVersion") {
    doLast {
        println("🚀 Building version: $version")
    }
}

tasks.register("setVersion") {
    doLast {
        val newVersion = project.findProperty("newVersion") as String
        val file = file("gradle.properties")

        val updated = file.readLines().map {
            if (it.startsWith("version=")) {
                "version=$newVersion"
            } else it
        }

        file.writeText(updated.joinToString("\n"))

        println("✅ Version updated to $newVersion")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = findProperty("artifactId") as String
            version = project.version.toString()

            pom {
                name.set(findProperty("name") as String)
                description.set("$description")
                url.set(findProperty("url") as String)

                licenses {
                    license {
                        name.set("GNU General Public License v3.0 or later")
                        url.set("https://www.gnu.org/licenses/gpl-3.0-standalone.html")
                    }
                }

                developers {
                    developer {
                        id.set("caleb-gyamfi")
                        name.set("Caleb Gyamfi")
                        email.set("caleb-g@omnixys.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/omnixys/logger-java.git")
                    developerConnection.set("scm:git:ssh://github.com:omnixys/logger-java.git")
                    url.set("https://github.com/omnixys/logger-java")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"

            url = uri("https://maven.pkg.github.com/omnixys/logger-java")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                    ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN")
                    ?: project.findProperty("gpr.key") as String?
            }
        }
    }
}
