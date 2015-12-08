package com.studyblue.qa.selenium.commons.v2.helper.scenario;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class ScenarioScreenShot {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioScreenShot.class);

    private final Supplier<Scenario> scenarioSuplier;
    private final WebDriver webDriver;

    public ScenarioScreenShot(Supplier<Scenario> scenarioSuplier, WebDriver webDriver) {
        this.scenarioSuplier = checkNotNull(scenarioSuplier);
        this.webDriver = checkNotNull(webDriver);
    }

    public void screenShot(String text) {
        if (webDriver instanceof TakesScreenshot) {
            scenarioSuplier.get().write(text);
            byte[] srcBytes = getSceenShot((TakesScreenshot) webDriver);
            scenarioSuplier.get().embed(srcBytes, "image/png");
        } else {
            LOGGER.warn("Screenshot is not allowed by this driver, [{}]", webDriver);
        }
    }

    public void screenShot() {
        if (webDriver instanceof TakesScreenshot) {
            byte[] srcBytes = getSceenShot((TakesScreenshot) webDriver);
            scenarioSuplier.get().embed(srcBytes, "image/png");
        } else {
            LOGGER.warn("Screenshot is not allowed by this driver, [{}]", webDriver);
        }
    }

    public void screenShot(Throwable exception) {
        if (webDriver instanceof TakesScreenshot) {
            try {
                scenarioSuplier.get().write(exception.getMessage());
                byte[] srcBytes = getSceenShot(exception, (TakesScreenshot) webDriver);
                if (srcBytes != null) {
                    scenarioSuplier.get().embed(srcBytes, "image/png");
                }
            } catch (RuntimeException e) {
                LOGGER.error("Exception while taking snapshot: ", e);
            }
        } else {
            LOGGER.warn("Screenshot is not allowed by this driver, [{}]", webDriver);
        }
    }

    private byte[] getSceenShot(Throwable throwable, TakesScreenshot driver) {
        Throwable cause = throwable.getCause();
        // can be disableled with -Dwebdriver.remote.quietExceptions=true
        if (cause instanceof ScreenshotException && StringUtils.isNotBlank(((ScreenshotException) cause).getBase64EncodedScreenshot())) {
            return Base64.decodeBase64(((ScreenshotException) cause).getBase64EncodedScreenshot());
        }
        return getSceenShot(driver);
    }

    private byte[] getSceenShot(TakesScreenshot driver) {
        try {
            return driver.getScreenshotAs(OutputType.BYTES);
        } catch (RuntimeException e) {
            LOGGER.warn("Cannot take Screenshot", e);
        }
        return null;
    }

}
