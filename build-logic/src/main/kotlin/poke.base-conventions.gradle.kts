plugins {
    `java-library`
}

group = "run.slicer"
version = "1.0.5"
description = "A Java library for performing bytecode normalization and generic deobfuscation."

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}
