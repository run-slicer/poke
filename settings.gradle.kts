pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "poke"

fun includePrefixed(vararg modules: String) {
    modules.forEach { module ->
        val name = "${rootProject.name}-${module}"

        include(name)
        project(":${name}").projectDir = file(module)
    }
}

includePrefixed("core", "cli")
