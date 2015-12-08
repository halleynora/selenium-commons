package com.studyblue.qa.selenium.commons.v2.helper.impl;

import com.studyblue.qa.selenium.commons.v2.helper.ActionsFactory;
import com.studyblue.qa.selenium.commons.v2.helper.WebDriverWaitFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.HasInputDevices;

import com.studyblue.qa.selenium.commons.v2.helper.SeleniumSupport;

public class SeleniumSupportFactoryImpl implements SeleniumSupport {

    private final WebDriver webDriver;

    public SeleniumSupportFactoryImpl(WebDriver webDriver) {
        super();
        this.webDriver = webDriver;
    }

    @Override
    public JavascriptExecutor getJavascriptExecutor() {
        if (webDriver instanceof JavascriptExecutor) {
            return (JavascriptExecutor) webDriver;
        }
        throw new RuntimeException("webDriver is not an instance of JavascriptExecutor");
    }

    @Override
    public SearchContext getSearchContext() {
        return webDriver;
    }

    @Override
    public ActionsFactory getActionsFactory() {
        return new ActionsFactoryImpl(webDriver);
    }

    @Override
    public WebDriverWaitFactory getWebDriverWaitFactory() {
        return new WebDriverWaitFactoryImpl(webDriver);
    }
    
    @Override
    public HasInputDevices getImputDevices() {
        return (HasInputDevices)webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    @Override
    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return webDriver.navigate();
    }

}
