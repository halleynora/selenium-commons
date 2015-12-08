package com.studyblue.qa.selenium.commons.v2.listener;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class ExceptionWebDriverEventListener extends AbstractWebDriverEventListener {

    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionWebDriverEventListener.class);

    private final Supplier<Scenario> scenarioSupplier;

    public ExceptionWebDriverEventListener(Supplier<Scenario> scenarioSupplier) {
        super();
        this.scenarioSupplier = checkNotNull(scenarioSupplier);
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {
        if (driver instanceof TakesScreenshot) {
            byte[] screenShot = getSceenShot(throwable, (TakesScreenshot) driver);
            if (screenShot != null) {
                scenarioSupplier.get().embed(screenShot, "image/png");
            }
        } else {
            LOGGER.warn("Cannot take Screenshot, driver {} do not implement TakesScreenshot", driver, throwable);
        }
        LOGGER.error("Error in webdriver, {}", throwable.getMessage(), throwable);
    }

    private byte[] getSceenShot(Throwable throwable, TakesScreenshot driver) {
        Throwable cause = throwable.getCause();
        // can be disableled with -Dwebdriver.remote.quietExceptions=true
        if (cause instanceof ScreenshotException && StringUtils.isNotBlank(((ScreenshotException) cause).getBase64EncodedScreenshot())) {
            return Base64.decodeBase64(((ScreenshotException) cause).getBase64EncodedScreenshot());
        }
        try {
            return driver.getScreenshotAs(OutputType.BYTES);
        } catch (RuntimeException e) {
            LOGGER.warn("Cannot take Screenshot", e);
        }
        return null;
    }

}
