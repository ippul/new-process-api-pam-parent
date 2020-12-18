package com.redhat.example;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:new-api-test-case-scenarios.feature",
plugin = { "json:target/junit-tests-cucumber.json", "pretty","html:target/cucumber-reports.html" } )
public class NewAPIRunnerTest {
}
