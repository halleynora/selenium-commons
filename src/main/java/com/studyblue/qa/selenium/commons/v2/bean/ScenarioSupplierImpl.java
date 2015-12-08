package com.studyblue.qa.selenium.commons.v2.bean;

import cucumber.api.Scenario;

public class ScenarioSupplierImpl implements ScenarioSupplier{
    
    private Scenario scenario;
    
    @Override
    public Scenario get() {
        return scenario;
    }

    @Override
    public void set(Scenario scenario) {
        this.scenario = scenario;
    }

}
