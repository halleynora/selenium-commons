package com.studyblue.qa.selenium.commons.v2.helper.impl;

import java.util.concurrent.TimeUnit;

import com.studyblue.qa.selenium.commons.v2.helper.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class WaitHelperImpl implements WaitHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitHelperImpl.class);

    private static final long DEFAULT_TIMEOUT = 2000;
    public final static long DEFAULT_SLEEP_TIMEOUT = 500;

    private final WebDriver webDriver;

    public WaitHelperImpl(WebDriver webDriver) {
        super();
        this.webDriver = webDriver;
    }

    @Override
    public void waitForElementNotVisible(By locator) {
        waitForElementNotVisible(locator, DEFAULT_TIMEOUT);
    }

    @Override
    public void waitForElementNotVisible(WebElement webElement) {
        waitForElementNotVisible(webElement, DEFAULT_TIMEOUT);
    }

    @Override
    public void waitForElementNotVisible(By locator, long timeout) {
        new WebDriverWait(webDriver, timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    @Override
    public void waitForElementNotVisible(WebElement webElement, long timeout) {

        new FluentWait<WebElement>(webElement, new SystemClock(), Sleeper.SYSTEM_SLEEPER) //
                .withTimeout(timeout, TimeUnit.SECONDS) //
                .pollingEvery(DEFAULT_SLEEP_TIMEOUT, TimeUnit.MILLISECONDS) //
                .ignoring(NotFoundException.class) //
                .until(new Predicate<WebElement>() {

                    @Override
                    public boolean apply(WebElement element) {
                        if (element != null && LOGGER.isDebugEnabled()) {
                            LOGGER.debug("element[{}] isDisplayed={}", element, element.isDisplayed());
                        }
                        return element == null || !element.isDisplayed();
                    }
                });
    }

    @Override
    public void waitForElementVisible(WebElement webElement) {
        waitForElementVisible(webElement, DEFAULT_TIMEOUT);
    }

    @Override
    public WebElement waitForElementVisible(By locator) {
        return waitForElementVisible(locator, DEFAULT_TIMEOUT);
    }

    @Override
    public void waitForElementVisible(WebElement webElement, long timeout) {

        new FluentWait<WebElement>(webElement, new SystemClock(), Sleeper.SYSTEM_SLEEPER) //
                .withTimeout(timeout, TimeUnit.SECONDS) //
                .pollingEvery(DEFAULT_SLEEP_TIMEOUT, TimeUnit.MILLISECONDS) //
                .ignoring(NotFoundException.class) //
                .until(new Predicate<WebElement>() {

                    @Override
                    public boolean apply(WebElement element) {
                        if (element != null && LOGGER.isDebugEnabled()) {
                            LOGGER.debug("element[{}] isDisplayed={} isEnabled= {}", element, element.isDisplayed(), element.isEnabled());
                        }
                        return element != null && element.isDisplayed() && element.isEnabled();
                    }
                });

    }
    
    @Override
    public void waitUntilClick(WebElement webElement) {
        waitUntilClick(webElement, DEFAULT_TIMEOUT);
    }
    
    @Override
    public void waitUntilClick(WebElement webElement, long timeout) {
        new FluentWait<WebElement>(webElement) //
                .ignoring(WebDriverException.class) //
                .withTimeout(timeout, TimeUnit.SECONDS)
                .pollingEvery(DEFAULT_SLEEP_TIMEOUT, TimeUnit.MILLISECONDS) //
                .until(new Click());

    }

    @Override
    public WebElement waitForElementVisible(By locator, long timeout) {
        return new WebDriverWait(webDriver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public WebElement waitForElementPresent(By locator, long timeout) {
        return new WebDriverWait(webDriver, timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    class Click implements Predicate<WebElement> {

        @Override
        public boolean apply(WebElement element) {
            LOGGER.debug("will click on element[{}] isDisplayed={} isEnabled= {}", element, element.isDisplayed(), element.isEnabled());
            element.click();
            return true;
        }

    }

}
