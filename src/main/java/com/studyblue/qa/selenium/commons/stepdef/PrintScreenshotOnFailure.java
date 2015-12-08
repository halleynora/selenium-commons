package com.studyblue.qa.selenium.commons.stepdef;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.studyblue.qa.selenium.commons.v2.helper.SeleniumHelperFactory;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.runtime.ScenarioImpl;
import gherkin.formatter.model.Result;

public class PrintScreenshotOnFailure {

    @Autowired
    private SeleniumHelperFactory seleniumHelperFactory;

    @After
    public void printScreenshotOnFailure(Scenario scenario) {
        if (scenario != null && scenario.isFailed()) {
            List<Result> stepResults = getResultList(scenario);
            if (stepResults.isEmpty()) {
                seleniumHelperFactory.getScenarioScreenShot().screenShot("Post-execution automatic screenshot upon failure.");
            } else {
                scenario.write("Post-execution automatic screenshot upon failure.");
                for (Result result : stepResults) {
                    if (result.getError() != null) {
                        seleniumHelperFactory.getScenarioScreenShot().screenShot(result.getError());
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Result> getResultList(Scenario scenario) {
        // hack to find to get the real exception and the embedded image if any
        if (scenario instanceof ScenarioImpl) {
            Field field = findField(ScenarioImpl.class, "stepResults");

            if (field != null) {
                makeAccessible(field);
                return (List<Result>) getField(field, scenario);
            }
        }
        return Collections.emptyList();
    }

}
