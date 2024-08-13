plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(rootProject.name)
                description.set(rootProject.description)
                url.set("https://github.com/run-slicer/poke")
                licenses {
                    license {
                        name.set("GNU General Public License v2.0")
                        url.set("https://github.com/run-slicer/poke/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("zlataovce")
                        name.set("Matouš Kučera")
                        email.set("mk@kcra.me")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/run-slicer/poke.git")
                    developerConnection.set("scm:git:ssh://github.com/run-slicer/poke.git")
                    url.set("https://github.com/run-slicer/poke/tree/main")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/run-slicer/poke")
            credentials {
                username = "run-slicer"
                password = System.getenv("GH_TOKEN")
            }
        }
    }
}
