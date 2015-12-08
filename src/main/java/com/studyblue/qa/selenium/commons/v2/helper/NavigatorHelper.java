package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface NavigatorHelper {

    void waitForPageLoad(String url, final String title, long timeToWaitInSecond);

    void loadPage(String url);

    void navigateTo(String url);

    void waitForPageReady(long timeToWaitInMillis) ;

    String getBaseURL();

    String getCurrentURL();

    void refresh();

    String getCurrentUrl();

    void scrollIntoView(WebElement webElement);

    void switchToFrameAndWaitForElement(String frame, By locator);

}
