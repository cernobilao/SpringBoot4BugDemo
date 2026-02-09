plugins {
	java
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
	implementation("org.hibernate.orm:hibernate-core:7.1.8.Final")
	implementation("com.oracle.database.jdbc:ojdbc11:23.9.0.25.07")
	implementation("org.eclipse:yasson:3.0.4")

	testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")
	testImplementation("org.hibernate.validator:hibernate-validator-test-utils:9.1.0.Final")
	testImplementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")
	testImplementation("org.glassfish.expressly:expressly:6.0.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
