import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("java")
    id("org.teavm") version "0.12.3"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven {
        name = "minecraft-libraries"
        url = uri("https://libraries.minecraft.net/")
    }
}

sourceSets {
    named("main") {
        java.srcDirs("port-src/minecraft-26.1.1")
        resources.srcDirs("assets", "data")
    }
}

dependencies {
    teavm("org.teavm:teavm-jso:0.12.3")
    teavm("org.teavm:teavm-jso-apis:0.12.3")

    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.mojang:authlib:7.0.63")
    implementation("com.mojang:brigadier:1.3.10")
    implementation("com.mojang:datafixerupper:9.0.19")
    implementation("com.mojang:logging:1.6.11")
    implementation("com.ibm.icu:icu4j:77.1")
    implementation("it.unimi.dsi:fastutil:8.5.18")
    implementation("org.joml:joml:1.10.8")
    implementation("org.jspecify:jspecify:1.0.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("commons-io:commons-io:2.20.0")
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.2")
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-commons:9.7.1")
    implementation("org.ow2.asm:asm-tree:9.7.1")
    implementation("org.ow2.asm:asm-util:9.7.1")
    implementation("org.ow2.asm:asm-analysis:9.7.1")
    implementation("com.github.oshi:oshi-core:6.9.0")
    implementation("com.mojang:jtracy:1.0.37")
    implementation("org.lz4:lz4-java:1.8.0")
    implementation("io.netty:netty-common:4.2.7.Final")
    implementation("io.netty:netty-buffer:4.2.7.Final")
    implementation("io.netty:netty-codec:4.2.7.Final")
    implementation("io.netty:netty-transport:4.2.7.Final")
    implementation("io.netty:netty-handler:4.2.7.Final")
    implementation("io.netty:netty-resolver:4.2.7.Final")
    implementation("io.netty:netty-codec-http:4.2.7.Final")
    implementation("io.netty:netty-transport-classes-epoll:4.2.7.Final")
    implementation("io.netty:netty-transport-classes-kqueue:4.2.7.Final")
    implementation("io.netty:netty-transport-native-unix-common:4.2.7.Final")
    implementation("io.netty:netty-transport-native-epoll:4.2.7.Final")
    implementation("io.netty:netty-transport-native-kqueue:4.2.7.Final")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
    implementation("net.sf.jopt-simple:jopt-simple:6.0-alpha-3")
    implementation("org.jcraft:jorbis:0.0.17")
    implementation("net.java.dev.jna:jna:5.17.0")
    implementation("net.java.dev.jna:jna-platform:5.17.0")
    implementation("org.lwjgl:lwjgl:3.4.1")
    implementation("org.lwjgl:lwjgl-glfw:3.4.1")
    implementation("org.lwjgl:lwjgl-openal:3.4.1")
    implementation("org.lwjgl:lwjgl-opengl:3.4.1")
    implementation("org.lwjgl:lwjgl-stb:3.4.1")
    implementation("org.lwjgl:lwjgl-tinyfd:3.4.1")
    implementation("org.lwjgl:lwjgl-freetype:3.4.1")
    implementation("org.lwjgl:lwjgl-jemalloc:3.4.1")

    implementation(files(
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/ca/weblite/java-objc-bridge/1.1/java-objc-bridge-1.1.jar",
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/com/microsoft/azure/msal4j/1.23.1/msal4j-1.23.1.jar",
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/com/mojang/blocklist/1.0.10/blocklist-1.0.10.jar",
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/com/mojang/javabridge/2.0.25/javabridge-2.0.25.jar",
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/com/mojang/text2speech/1.18.11/text2speech-1.18.11.jar",
        "C:/Users/jonat/AppData/Roaming/.minecraft/libraries/org/apache/commons/commons-compress/1.28.0/commons-compress-1.28.0.jar"
    ))
}

teavm {
    all {
        outOfProcess = false
        processMemory = 4096
        optimization = OptimizationLevel.BALANCED
        debugInformation = true
        fastGlobalAnalysis = false
    }
}

teavm.js {
    mainClass = "net.minecraft.client.main.Main"
    outputDir = file("build/javascript")
    targetFileName = "classes.js"
    obfuscated = false
    sourceMap = true
    entryPointName.set("main")
    properties = mapOf("java.util.TimeZone.autodetect" to "true")
}

teavm.wasm {
    mainClass = "net.minecraft.client.main.Main"
    outputDir = file("build/wasm")
    targetFileName = "classes.wasm"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.add("--enable-preview")
}
