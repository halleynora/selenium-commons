package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;

public class WebDriverUtil {
    
    public static WebDriver getUnwrappedWebDriver(WebDriver webDriver) {
        if (webDriver instanceof WrapsDriver) {
            return ((WrapsDriver) webDriver).getWrappedDriver();
        }
        return webDriver;
    }
    
    public static WebElement getUnwrappedWebElement(WebElement webElement) {
        if (webElement instanceof WrapsElement) {
            return ((WrapsElement) webElement).getWrappedElement();
        }
        return webElement;
    }
}
