plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Spigot API (make sure this file exists in the libs folder)
    compileOnly(files("libs/spigot-api-1.21.4-R0.1-SNAPSHOT.jar"))

    // Gson (Google JSON library)
    implementation("com.google.code.gson:gson:2.10.1") // Latest version
}

tasks.test {
    useJUnitPlatform()
}

// Set Java version
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // Ensure compatibility with Minecraft versions
}

// Automatically copy the built JAR to IntelliJ project directory: /server/plugins/
tasks.register<Copy>("copyToIntelliJServer") {
    dependsOn(tasks.build)
    from(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    into(file("${rootProject.rootDir}/server/plugins/")) // Target IntelliJ's directory
}

// Ensure the JAR is copied after build
tasks.build {
    finalizedBy("copyToIntelliJServer")
}
