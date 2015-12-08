package com.studyblue.qa.selenium.commons.v2.helper.impl;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Supplier;
import com.studyblue.qa.selenium.commons.v2.helper.ActionHelper;
import com.studyblue.qa.selenium.commons.v2.helper.NavigatorHelper;
import com.studyblue.qa.selenium.commons.v2.helper.SeleniumHelperFactory;
import com.studyblue.qa.selenium.commons.v2.helper.VisibilityHelper;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioGridInfoPrinter;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioHelper;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioScreenShot;

import cucumber.api.Scenario;

public class SeleniumHelperFactoryImpl implements SeleniumHelperFactory {

    protected final WebDriver webDriver;
    protected final Supplier<Scenario> scenarioSupplier;

    public SeleniumHelperFactoryImpl(WebDriver webDriver, Supplier<Scenario> scenarioSupplier) {
        super();
        this.webDriver = webDriver;
        this.scenarioSupplier = scenarioSupplier;
    }

    @Override
    public ActionHelper getActionHelper() {
        return new ActionHelperImpl(webDriver);
    }

    @Override
    public NavigatorHelper getNavigatorHelper() {
        return new NavigatorHelperImpl(webDriver);
    }

    @Override
    public ScenarioGridInfoPrinter getScenarioGridInfoPrinter() {
        return new ScenarioGridInfoPrinter(scenarioSupplier, webDriver);
    }

    @Override
    public ScenarioScreenShot getScenarioScreenShot() {
        return new ScenarioScreenShot(scenarioSupplier, webDriver);
    }

    @Override
    public ScenarioHelper getScenarioHelper() {
        return new ScenarioHelper(scenarioSupplier);
    }

    @Override
    public WaitHelperImpl getWaitHelper() {
        return new WaitHelperImpl(webDriver);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public Supplier<Scenario> getScenarioSupplier() {
        return scenarioSupplier;
    }

    @Override
    public VisibilityHelper getVisibilityHelper() {
        return new VisibilityHelperImpl(webDriver, getScenarioScreenShot());
    }

}
