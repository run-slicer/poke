plugins {
    id("poke.base-conventions")
    alias(libs.plugins.teavm)
}

dependencies {
    implementation(project(":${rootProject.name}-core"))
    compileOnly(libs.teavm.core)
}

teavm.js {
    mainClass = "run.slicer.poke.js.Main"
    moduleType = org.teavm.gradle.api.JSModuleType.ES2015
    // obfuscated = false
    // optimization = org.teavm.gradle.api.OptimizationLevel.NONE
}

tasks {
    register<Copy>("copyDist") {
        group = "build"

        from("../README.md", "../LICENSE", generateJavaScript, "poke.d.ts")
        into("dist")

        doLast {
            file("dist/package.json").writeText(
                """
                    {
                      "name": "@run-slicer/poke",
                      "version": "${project.version}",
                      "description": "A library for performing Java bytecode normalization and generic deobfuscation.",
                      "main": "poke-js.js",
                      "types": "poke.d.ts",
                      "keywords": [
                        "deobfuscation",
                        "java",
                        "bytecode",
                        "optimization"
                      ],
                      "author": "run-slicer",
                      "license": "GPL-2.0-only"
                    }
                """.trimIndent()
            )
        }
    }

    build {
        dependsOn("copyDist")
    }
}
