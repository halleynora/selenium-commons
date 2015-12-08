package com.studyblue.qa.selenium.commons.v2.helper.impl;

import com.studyblue.qa.selenium.commons.v2.helper.ActionHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ActionHelperImpl implements ActionHelper {

    private final WebDriver webDriver;

    public ActionHelperImpl(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public void mouseOver(By locator) {
        mouseOver(webDriver.findElement(locator));
    }

    @Override
    public void mouseOver(WebElement webElement) {
        new Actions(webDriver).moveToElement(webElement).build().perform();
    }

    @Override
    public void mouseOverJS(By locator) {
        mouseOverJS(webDriver.findElement(locator));
    }

    @Override
    public void mouseOverJS(WebElement webElement) {
        javaScriptExecute(webElement, "arguments[0].onmouseover();");
    }

    @Override
    public void mouseOutJS(By locator) {
        mouseOutJS(webDriver.findElement(locator));
    }

    @Override
    public void mouseOutJS(WebElement webElement) {
        javaScriptExecute(webElement, "arguments[0].onmouseout();");
    }

    private Object javaScriptExecute(WebElement webElement, String javaScriptCode) {
        if (webDriver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            return js.executeScript(javaScriptCode, webElement);
        }
        throw new UnsupportedOperationException("webDriver do not implement JavascriptExecutor");
    }

    @Override
    public void moveToElement(By xpath) {
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xpath));
        Actions action = new Actions(webDriver);
        action.moveToElement(webDriver.findElement(xpath)).build().perform();
    }

}
