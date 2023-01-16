import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.SteveTheEngineer.SS-BukkitGradle") version "1.4"
}

group = "me.ste.stevesseries"
version = "0.0.2-mc1.19.3"
description = "A plugin that lets you customize how your dropped items look in the world."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    pluginRuntimeOnly("com.github.SteveTheEngineer:SS-Kotlin:1.8.0")

    implementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    depend("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
}

runServer {
    downloadUri.set("https://api.papermc.io/v2/projects/paper/versions/1.19.3/builds/378/downloads/paper-1.19.3-378.jar")
}

pluginDescription {
    mainClass.set("me.ste.stevesseries.fancydrops.FancyDrops")
    apiVersion.set("1.19")
    authors.add("SteveTheEngineer")
}

pluginCommands {
    command("fancydropsreload", usage = "/fancydropsreload", description = "Reloads the item presets and messages.")
    command("fancydropstestconfig", usage = "/fancydropstestconfig", description = "Runs some tests to verify the currently active configuration.")
}

pluginPermissions {
    permission("stevesseries.fancydrops.reload", description = "Permission to use the /fancydropsreload command.")
    permission("stevesseries.fancydrops.testconfig", description = "Permission to use the /fancydropstestconfig command.")
}
