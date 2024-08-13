plugins {
    id("poke.base-conventions")
    id("poke.publish-conventions")
}

dependencies {
    implementation(libs.proguard)
    compileOnly(libs.jspecify)
}
