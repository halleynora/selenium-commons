package com.studyblue.qa.selenium.commons.stepdef;

import com.studyblue.qa.selenium.commons.v2.helper.SeleniumHelperFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.studyblue.qa.selenium.commons.v2.bean.ScenarioSupplier;

import cucumber.api.Scenario;
import cucumber.api.java.Before;

/**
 * extends or add com.studyblue.qa.selenium.commons.stepdef in your stepdef to enable
 *
 */
public class ScenarioInitializer {

    @Autowired
    private ScenarioSupplier scenarioSupplier;
    
    @Autowired
    private SeleniumHelperFactory seleniumHelperFactory;
    
    @Before
    public void initScenario(Scenario scenario){
        scenarioSupplier.set(scenario);
        
    }
    
    @Before
    public void printGridInfoToLog(Scenario scenario){
        seleniumHelperFactory.getScenarioGridInfoPrinter().printGridInfoToLog();
    }
    
//    @Before
    /**
     * TODO: bug in json reporter, cannot write anything in scenario in a before state see https://github.com/cucumber/gherkin/issues/338
     */
    public void printGridInfo(Scenario scenario){
        seleniumHelperFactory.getScenarioGridInfoPrinter().printGridInfo();
    }

}
