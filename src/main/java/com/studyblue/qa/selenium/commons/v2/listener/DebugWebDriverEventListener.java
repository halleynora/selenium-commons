package com.studyblue.qa.selenium.commons.v2.listener;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugWebDriverEventListener implements WebDriverEventListener {
    
    private static Logger LOGGER = LoggerFactory.getLogger(DebugWebDriverEventListener.class);


    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        LOGGER.debug("beforeNavigateTo {}", url);
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        LOGGER.debug("afterNavigateTo {}", url);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        LOGGER.debug("beforeNavigateBack {}", driver);
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {
        LOGGER.debug("afterNavigateBack {}", driver);
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        LOGGER.debug("beforeNavigateForward {}", driver);
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {
        LOGGER.debug("afterNavigateForward {}", driver);
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        LOGGER.debug("beforeFindBy {} {}", by, element);
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        LOGGER.debug("afterFindBy {} {}", by, element);
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        LOGGER.debug("beforeClickOn {}", element);
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        LOGGER.debug("afterClickOn {}", element);
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {
        LOGGER.debug("beforeChangeValueOf {}", element);
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        LOGGER.debug("afterChangeValueOf {}", element);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        LOGGER.debug("beforeScript {}", script);
    }

    @Override
    public void afterScript(String script, WebDriver driver) {
        LOGGER.debug("afterScript {}", script);
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {
        LOGGER.debug("onException", throwable);
    }

}
