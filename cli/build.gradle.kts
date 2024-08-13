plugins {
    id("poke.base-conventions")
    id("poke.publish-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.blossom)
    application
}

dependencies {
    api(project(":${rootProject.name}-core"))
    compileOnly(libs.jspecify)
    implementation(libs.picocli)
    annotationProcessor(libs.picocli.codegen)
}

tasks {
    withType<Jar> {
        manifest {
            attributes("Main-Class" to "run.slicer.poke.cli.Main")
        }
    }

    shadowJar {
        minimize()
    }
}

application {
    mainClass = "run.slicer.poke.cli.Main"
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}
