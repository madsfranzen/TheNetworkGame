plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
}

repositories { 
    mavenCentral()
    // Add Sonatype snapshots repository for latest JavaFX releases
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

val javaFXVersion = "21.0.2"

dependencies {
    implementation("com.google.guava:guava:32.0.1-jre")

    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20250107")
    
    // JavaFX dependencies with explicit versions
    implementation("org.openjfx:javafx-controls:$javaFXVersion")
    implementation("org.openjfx:javafx-fxml:$javaFXVersion")
    implementation("org.openjfx:javafx-base:$javaFXVersion")
    implementation("org.openjfx:javafx-graphics:$javaFXVersion")
    implementation("org.openjfx:javafx-media:$javaFXVersion")
    implementation("org.openjfx:javafx-swing:$javaFXVersion")
    implementation("org.openjfx:javafx-web:$javaFXVersion")
}

javafx {
    version = javaFXVersion
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.base",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "javafx.web"
    )
}

java {
    toolchain { 
        languageVersion.set(JavaLanguageVersion.of(23))  // Match your installed Java version
    }
}

sourceSets {
    main {
        java.srcDir("src/main/java")
        resources {
            srcDir("src/main/resources")
            include("**/*.png", "**/*.jpg", "**/*.fxml", "**/*.css", "**/*.properties", "**/*.json")
        }
    }
}

tasks {
    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    
    withType<JavaExec> {
        standardInput = System.`in`
        jvmArgs = listOf(
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication"
        )
    }
    
    processResources {
        filesMatching("**/*.properties") {
            expand(project.properties)
        }
    }
}

application { 
    mainClass.set("App")
    applicationDefaultJvmArgs = listOf("-Xmx2g")
}
