plugins {
    application
    alias(libs.plugins.jooq)
    alias(libs.plugins.spring.boot)
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.guava)

    implementation(libs.jooq.core)
    runtimeOnly(libs.postgresql)
    jooqGenerator(libs.postgresql)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.jooq)
}

application {
    mainClass = "org.amts.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

jooq {
    version.set(libs.versions.jooqVersion.get())
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://127.0.0.1:54322/postgres" // Local Supabase
                    user = "postgres"
                    password = "postgres"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        excludes = ""
                        inputSchema = "public"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "org.amts.jooq"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}
