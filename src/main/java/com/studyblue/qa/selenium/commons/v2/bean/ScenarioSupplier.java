package com.studyblue.qa.selenium.commons.v2.bean;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public interface ScenarioSupplier extends Supplier<Scenario>{
   
    void set(Scenario scenario);
}
