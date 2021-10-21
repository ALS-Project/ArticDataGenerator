plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

dependencies {
    implementation(project(":DataGenerator:1.16.5"))
    implementation(project(":DataGenerator:1.17"))
    implementation(project(":DataGenerator:core"))
}

minecraft {
    version("21w42a")
    platform(org.spongepowered.gradle.vanilla.repository.MinecraftPlatform.SERVER)
}