package com.studyblue.qa.selenium.commons.v2.helper.impl;

import com.studyblue.qa.selenium.commons.v2.helper.WebDriverWaitFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverWaitFactoryImpl implements WebDriverWaitFactory {
    
    private static final long DEFAULT_TIMEOUT_IN_SECOND = 60;
    
    private final long timeOutInSeconds;

    private final WebDriver webDriver;

    public WebDriverWaitFactoryImpl(WebDriver webDriver) {
        this(webDriver, DEFAULT_TIMEOUT_IN_SECOND);
    }
    
    public WebDriverWaitFactoryImpl(WebDriver webDriver, long timeOutInSeconds) {
        super();
        this.webDriver = webDriver;
        this.timeOutInSeconds = timeOutInSeconds;
    }

    @Override
    public WebDriverWait createWebDriverWait(long timeOutInSeconds) {
        return new WebDriverWait(webDriver, timeOutInSeconds);
    }
    
    @Override
    public WebDriverWait createWebDriverWait() {
        return new WebDriverWait(webDriver, timeOutInSeconds);
    }

}
