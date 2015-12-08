package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.HasInputDevices;

public interface SeleniumSupport {

    JavascriptExecutor getJavascriptExecutor();

    SearchContext getSearchContext();

    ActionsFactory getActionsFactory();

    WebDriverWaitFactory getWebDriverWaitFactory();

    HasInputDevices getImputDevices();
    
    TargetLocator switchTo();

    Navigation navigate();

}