package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ActionHelper {

    void mouseOver(By locator);

    void mouseOver(WebElement webElement);

    void mouseOverJS(By locator) ;

    void mouseOverJS(WebElement webElement) ;

    void mouseOutJS(By locator);

    void mouseOutJS(WebElement webElement);

    void moveToElement(By xpath);

}
