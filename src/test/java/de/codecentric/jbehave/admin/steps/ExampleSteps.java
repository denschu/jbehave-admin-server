package de.codecentric.jbehave.admin.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class ExampleSteps {

	@Given("a Request $request")
	public void givenRequest(@Named("request") String request) {

	}

	@When("the API $api is called")
	public void whenApiCalled(@Named("api") String api) throws InterruptedException {
		Thread.sleep(2000);
	}

	@Then("the response matches $response")
	public void thenResponseMatches(@Named("response") String response) {

	}

}