import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.0"
}

group = "com.snad.kvmapper"
version = "1.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)

    testImplementation("app.cash.turbine:turbine:0.7.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

compose.desktop {
    application {
        mainClass = "com.snad.kvmapper.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            packageName = "KVMapper"
            packageVersion = "1.1.0"
            description = "Key Value Mapper"
            copyright = "2022 SNAD. All rights reserved."

            macOS {
                iconFile.set(project.file("icon.icns"))
            }

            modules("java.instrument", "jdk.unsupported")
        }
    }
}