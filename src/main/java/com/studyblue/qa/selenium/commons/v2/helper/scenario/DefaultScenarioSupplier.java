package com.studyblue.qa.selenium.commons.v2.helper.scenario;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class DefaultScenarioSupplier implements Supplier<Scenario> {

    private Scenario scenario;

    public DefaultScenarioSupplier(Scenario scenario) {
        super();
        this.scenario = checkNotNull(scenario);
    }

    @Override
    public Scenario get() {
        return scenario;
    }
    
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

}
