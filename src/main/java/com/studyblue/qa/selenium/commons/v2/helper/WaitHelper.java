package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface WaitHelper {

    void waitForElementNotVisible(By locator);

    void waitForElementNotVisible(WebElement webElement);

    void waitForElementNotVisible(By locator, long timeout);

    void waitForElementNotVisible(WebElement webElement, long timeout);

    void waitForElementVisible(WebElement webElement);

    WebElement waitForElementVisible(By locator);

    void waitForElementVisible(WebElement webElement, long timeout);

    WebElement waitForElementVisible(By locator, long timeout);

    WebElement waitForElementPresent(By locator, long timeout);

    void waitUntilClick(WebElement webElement);

    void waitUntilClick(WebElement webElement, long timeout);

}
