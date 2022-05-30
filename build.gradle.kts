plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.dependencycheck)
    alias(libs.plugins.jib)
    application
    groovy
}

dependencies {
    implementation("commons-lang:commons-lang:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.5")
    implementation(libs.bundles.guice)
    implementation(libs.bundles.logging)
    implementation("com.sparkjava:spark-core:+")
    implementation(libs.graphql)
    implementation(libs.klaxon)
    implementation(libs.gson)

    testImplementation(libs.bundles.spock)
    testImplementation(libs.bundles.groovy)
}

application {
    mainClass.set("theagainagain.MainKt")
}

if(project.hasProperty("tags")) {
    val tags: String by project
    println("########### tags: $tags")
}

if(project.hasProperty("labels")) {
    val labels: String by project
    println("########### labels: $labels")
}

jib {
    from {
        image = "azul/zulu-openjdk-debian:11-latest"
    }
    to {
        image = if(project.hasProperty("toImage")) {
            val toImage: String by project
            toImage
        } else {
            "localhost"
        }
    }
}

tasks {
    register("stageVersion") {
        dependsOn("installDist")
        val version: String = System.getenv("SOURCE_VERSION") ?: "r999"
        doLast {
            File("$buildDir", "version").writeText(version)
        }
    }

    register("stage") {
        dependsOn("test", "stageVersion")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

allOpen {
    annotation("theagainagain.OpenForTesting")
}
