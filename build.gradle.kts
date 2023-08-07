import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import com.google.protobuf.gradle.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("com.google.protobuf") version "0.9.4"
}

group = "com.backend"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.4.4"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.backend.smolthing.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation("com.google.protobuf:protobuf-java:3.22.2")
  implementation("io.grpc:grpc-protobuf:1.53.0")

  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-grpc-server")
  implementation("javax.annotation:javax.annotation-api:1.3.2")

  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-service-proxy")
  implementation("io.vertx:vertx-health-check")
  implementation("io.vertx:vertx-grpc-context-storage")
  implementation("io.vertx:vertx-grpc-client")
  implementation("io.vertx:vertx-service-discovery")
  implementation("io.vertx:vertx-tcp-eventbus-bridge")
  implementation("io.vertx:vertx-opentracing")
  implementation("io.vertx:vertx-service-factory")
  implementation("io.vertx:vertx-web-sstore-cookie")
  implementation("io.vertx:vertx-web-sstore-redis")
  implementation("io.vertx:vertx-web-validation")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-zookeeper")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-http-service-factory")
  implementation("io.vertx:vertx-micrometer-metrics")
  implementation("io.vertx:vertx-json-schema")
  implementation("io.vertx:vertx-shell")
  implementation("io.vertx:vertx-rx-java3")
  implementation("io.vertx:vertx-redis-client")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-web-graphql")
  implementation("io.vertx:vertx-circuit-breaker")
  implementation("io.vertx:vertx-consul-client")
  implementation("io.vertx:vertx-kafka-client")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

protobuf {
  protoc {
    // The artifact spec for the Protobuf Compiler
    artifact = "com.google.protobuf:protoc:3.22.2"
  }
  plugins {
    // Optional: an artifact spec for a protoc plugin, with "grpc" as
    // the identifier, which can be referred to in the "plugins"
    // container of the "generateProtoTasks" closure.
    id("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:1.53.0"
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.plugins {
        // Apply the "grpc" plugin whose spec is defined above, without
        // options. Note the braces cannot be omitted, otherwise the
        // plugin will not be added. This is because of the implicit way
        // NamedDomainObjectContainer binds the methods.
        id("grpc") { }
      }
    }
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
