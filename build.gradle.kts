plugins {
    id("java")
//    id("application")
}

group = "com.github.jimschubert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

//application {
//    mainClass.set("com.github.jimschubert.docker.parser.DockerfileParser")
//    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8",
//        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
//        "--add-opens", "java.base/java.util=ALL-UNNAMED")
//}

tasks.test {
    useJUnitPlatform()
}