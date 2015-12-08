package com.studyblue.bdd.monitor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.studyblue.qa.selenium.commons.BrowserDriver;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * This class defines the glue that connects the feature files, the java Step Definitions and the configurations for the Cucumber Tags and Output format Use to
 * test grid
 *
 * 
 */

@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/results",
        "json:target/results.json" }, features = "src/test/resources/com/studyblue/bdd/", glue = { "com.studyblue.bdd.stepdefs.grid" },
        // tags={"@new"}
        tags = { "@smoke" })
public class GridMonitorRunner {

    @BeforeClass
    public static void setup() {
        System.out.println("Ran the before");
    }

    @AfterClass
    public static void teardown() {
        System.out.println("Ran the after");
        if (BrowserDriver.whereToRun != null && BrowserDriver.whereToRun.equalsIgnoreCase("local")) {
            // BrowserDriverSteps.displayHtmlResult();
        }
    }
}
