import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.backend"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.4.4"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.backend.starter.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-service-proxy")
  implementation("io.vertx:vertx-health-check")
  implementation("io.vertx:vertx-grpc-server")
  implementation("io.vertx:vertx-service-discovery")
  implementation("io.vertx:vertx-grpc-context-storage")
  implementation("io.vertx:vertx-tcp-eventbus-bridge")
  implementation("io.vertx:vertx-opentracing")
  implementation("io.vertx:vertx-grpc-client")
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
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
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
