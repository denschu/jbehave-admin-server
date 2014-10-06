package de.codecentric.jbehave.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import de.codecentric.jbehave.admin.config.EnableJBehaveAdminServer;

@EnableAutoConfiguration
@EnableJBehaveAdminServer
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(TestApplication.class);
		springApplication.run(args);
	}
}
