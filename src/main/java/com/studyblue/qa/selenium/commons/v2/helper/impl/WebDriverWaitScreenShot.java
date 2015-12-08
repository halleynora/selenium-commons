package com.studyblue.qa.selenium.commons.v2.helper.impl;

import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioScreenShot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;

public class WebDriverWaitScreenShot extends WebDriverWait {

    private boolean exceptionPropagated = false;
    private final Optional<ScenarioScreenShot> scenarioScreenShot;
    
    public WebDriverWaitScreenShot(WebDriver driver, long timeOutInSeconds, ScenarioScreenShot scenarioScreenShot) {
        super(driver, timeOutInSeconds);
        this.scenarioScreenShot = Optional.fromNullable(scenarioScreenShot);
    }

    @Override
    public <V> V until(Function<? super WebDriver, V> arg0) {
        try {
            return super.until(arg0);
        } catch (RuntimeException e) {
            takeScreenShot(e);
            throw Throwables.propagate(e);
        }

    }

    @Override
    public void until(Predicate<WebDriver> isTrue) {
        try {
            super.until(isTrue);
        } catch (RuntimeException e) {
            takeScreenShot(e);
            Throwables.propagate(e);
        }
    }

    private void takeScreenShot(RuntimeException e) {
        if (scenarioScreenShot.isPresent() && !exceptionPropagated) {
            exceptionPropagated = true;
            scenarioScreenShot.get().screenShot(e);
        }
    }

}
