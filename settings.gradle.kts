rootProject.name = "theagainagain"

val gprUsername = extra.has("gpr.user").let { if (it) extra.get("gpr.user") as String else System.getenv("GITHUB_USERNAME") as String}
val gprToken = extra.has("gpr.key").let { if (it) extra.get("gpr.key") as String else System.getenv("GITHUB_TOKEN") as String}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://maven.pkg.github.com/whodevil/jvm-platform")
            credentials {
                username = gprUsername
                password = gprToken
            }
        }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }

    versionCatalogs {
        create("libs") {
            from("info.offthecob.jvm.platform:catalog:v0.0.13")
            library("structured-logging", "info.offthecob.jvm.platform", "logging").version {
                strictly("v0.0.13")
            }
        }
    }
}

rootProject.projectDir.listFiles().filter { it.isDirectory }.map { subDir ->
    subDir.listFiles().filter {
        it.isFile && it.name.contains(".gradle.kts")
    }.map {
        File(it.parent).name
    }
}.flatten().forEach {
    include(it)
}

rootProject.children.forEach { project ->
    project.buildFileName = "${project.name}.gradle.kts"
    assert(project.buildFile.isFile)
}
