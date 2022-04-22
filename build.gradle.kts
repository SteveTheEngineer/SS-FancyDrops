import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.6.20"
    id("me.ste.stevesseries.bukkitgradle") version "1.0"
}

group = "me.ste.stevesseries"
version = "0.0.0-mc1.18.2"
description = "A plugin that lets you customize how your dropped items look in the world."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://mvn-public.steenesvc.cf/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    pluginRuntimeOnly("me.ste.stevesseries.kotlin:kotlin:1.6.20")

    implementation(files("run/versions/1.18.2/paper-1.18.2.jar"))
    implementation("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    depend("com.comphenix.protocol:ProtocolLib:4.7.0")
}

runServer {
    downloadUri.set("https://papermc.io/api/v2/projects/paper/versions/1.18.2/builds/301/downloads/paper-1.18.2-301.jar")
}

pluginDescription {
    mainClass.set("me.ste.stevesseries.fancydrops.FancyDrops")
    apiVersion.set("1.18")
    authors.add("SteveTheEngineer")
}

pluginCommands {
    command("fancydropsreload", usage = "/fancydropsreload", description = "Reloads the item presets.")
    command("fancydropstestconfig", usage = "/fancydropstestconfig", description = "Runs some tests to check the currently active configuration.")
}

pluginPermissions {
    permission("stevesseries.fancydrops.reload", description = "Permission to use the /fancydropsreload command.")
    permission("stevesseries.fancydrops.testconfig", description = "Permission to use the /fancydropstestconfig command.")
}
