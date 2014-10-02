JBehave Admin Server
================================================

[![Build Status](https://travis-ci.org/denschu/jbehave-admin.png?branch=master)](https://travis-ci.org/denschu/jbehave-admin)

Admin Application for managing JBehave-Stories and Executions

# Features

* Story Dashboard
* Execute Stories and get direct feedback
* External Story Repository (SVN)

# Build

	cd jbehave-admin
	mvn clean install

# Run

Add the following dependency to your pom.xml.

```
<dependency>
	<groupId>de.codecentric</groupId>
	<artifactId>jbehave-admin-server</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

Create the Admin Server with only one single Annotation.

```
@Configuration
@EnableAutoConfiguration
@EnableJBehaveAdminServer
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

Open GUI in the browser
	
	http://localhost:8080/
	
Test API

	curl http://localhost:8080/api/stories
		
	
# Backlog

* Versioned testdata and stories
* Run Story from Web GUI
