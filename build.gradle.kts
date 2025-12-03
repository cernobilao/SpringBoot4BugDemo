plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "cz.cernobilao"
version = "0.0.1-SNAPSHOT"
description = "Demo for Spring Boot 4.0.0 Exception "

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.oracle.database.jdbc:ojdbc11")
    implementation("org.eclipse:yasson")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    testImplementation("org.hibernate.validator:hibernate-validator-test-utils:9.1.0.Final")
    testImplementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")
    testImplementation("org.glassfish.expressly:expressly:6.0.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
