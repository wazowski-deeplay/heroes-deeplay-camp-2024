// Для текущего модуля и только
plugins {
    java
    id("net.ltgt.errorprone") version "4.0.1"
    jacoco
    checkstyle
}

group = "io.deeplay.camp"
version = "1.0-SNAPSHOT"

// Для всего проекта включая все модули
allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "net.ltgt.errorprone")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        // Тесты
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        errorprone("com.google.errorprone:error_prone_core:2.28.0")
        // логирование
        implementation("ch.qos.logback:logback-core:1.5.6")
        implementation("ch.qos.logback:logback-classic:1.5.6")
        implementation("org.slf4j:slf4j-api:2.0.13")
        // кодогенерация
        compileOnly("org.projectlombok:lombok:1.18.32")
        annotationProcessor("org.projectlombok:lombok:1.18.32")
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(false)
        }
    }

    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "10.17.0"
        configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    }

    tasks.withType<Checkstyle> {
        ignoreFailures = false
        reports {
            xml.required.set(false)
            html.required.set(true)
        }
    }

}

tasks.build {
    dependsOn(tasks.withType<Checkstyle>())
}