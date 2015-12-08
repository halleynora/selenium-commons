package com.studyblue.qa.selenium.commons.v2.helper.impl;

import static org.junit.Assert.fail;

import com.studyblue.qa.selenium.commons.v2.helper.VisibilityHelper;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioScreenShot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class VisibilityHelperImpl implements VisibilityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisibilityHelperImpl.class);

    private final WebDriver webDriver;
    private final ScenarioScreenShot scenarioScreenShot;

    public VisibilityHelperImpl(WebDriver webDriver, ScenarioScreenShot scenarioScreenShot) {
        super();
        this.webDriver = webDriver;
        this.scenarioScreenShot = scenarioScreenShot;
    }

    @Override
    public void failIfElementNotVisible(By by) {
        failIfElementNotVisible(by, "element + " + by + "is not visible");
    }

    @Override
    public void failIfElementNotVisible(By by, String message) {
        try {
            if (!webDriver.findElement(by).isDisplayed()) {
                scenarioScreenShot.screenShot(message);
                fail();
            }
        } catch (WebDriverException e) {
            // ignore exception, webDriver should has taken the screen shot
            LOGGER.debug("The WebElement defined as '{}' does not appear to be visible.", by, e);
            Throwables.propagate(e);
        }

    }

    @Override
    public boolean isElementVisible(By by) {
        try {
            return webDriver.findElement(by).isDisplayed();
        } catch (WebDriverException e) {
            return false;
        }

    }

    @Override
    public boolean isElementNotVisible(By by) {
        return !isElementVisible(by);
    }

    @Override
    public void failIfElementVisible(By by) {
        failIfElementVisible(by, "element + " + by + "is visible");
    }

    @Override
    public void failIfElementVisible(By by, String message) {
        try {
            if (webDriver.findElement(by).isDisplayed()) {
                scenarioScreenShot.screenShot("element + " + by + "is visible");
                fail();
            }
        } catch (WebDriverException e) {
            // ignore exception, webDriver should has taken the screen shot
            LOGGER.debug("The WebElement defined as '{}' does not appear to be visible.", by, e);
        }
    }

}
