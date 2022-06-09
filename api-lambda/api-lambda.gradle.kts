plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.dependencycheck)
    alias(libs.plugins.jib)
    groovy
}

jib {
    from {
        image = "azul/zulu-openjdk:11.0.14.1-11.54.25"
    }
    to {
        image = if(project.hasProperty("toImage")) {
            val toImage: String by project
            toImage
        } else {
            "localhost"
        }
        tags = if(project.hasProperty("tag")) {
            val tag: String by project
            setOf(tag)
        } else {
            setOf("latest")
        }
    }
    container {
        entrypoint = listOf(
            "/usr/bin/java",
            "-Dlogback.configurationFile=/app/resources/logback.xml",
            "-cp",
            "@/app/jib-classpath-file",
            "com.amazonaws.services.lambda.runtime.api.client.AWSLambda"
        )
        args = listOf("info.offthecob.lambda.Graphql::handleRequest")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(libs.bundles.logging)
    implementation(libs.graphql)
    implementation(libs.klaxon)
    implementation(libs.gson)
    implementation(libs.bundles.lambda)
    implementation(libs.structured.logging)

    testImplementation(libs.bundles.spock)
    testImplementation(libs.bundles.groovy)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
