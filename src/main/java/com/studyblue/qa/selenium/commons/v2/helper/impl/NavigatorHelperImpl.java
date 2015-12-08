package com.studyblue.qa.selenium.commons.v2.helper.impl;

import java.net.MalformedURLException;
import java.net.URL;

import com.studyblue.qa.selenium.commons.v2.helper.NavigatorHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigatorHelperImpl implements NavigatorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigatorHelperImpl.class);

    private final WebDriver webDriver;

    public NavigatorHelperImpl(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * Load a page and validate the title for the page returned
     * 
     * @param url URL to load
     * @param title Title of page that is loaded
     */
    @Override
    public void waitForPageLoad(String url, final String title, long timeToWaitInSecond) {
        loadPage(url);

        new WebDriverWait(webDriver, timeToWaitInSecond).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return d.getTitle().startsWith(title);
            }
        });
    }

    /**
     * Load a page without validation
     * 
     * @param url (String) URL to load
     */
    @Override
    public void loadPage(String url) {
        LOGGER.debug("Directing browser to: " + url);
        webDriver.get(url);
    }

    @Override
    public void navigateTo(String url) {
        LOGGER.debug("Navigate browser to: " + url);
        webDriver.navigate().to(url);
    }

    /**
     * Wait for page load. Until document.readyState = complete
     * @param timeToWaitInMillis
     */
    @Override
    public void waitForPageReady(long timeToWaitInMillis) {
        Object state = "NA";
        if (webDriver instanceof JavascriptExecutor) {
            long maxTime = System.currentTimeMillis() + timeToWaitInMillis;
            do {
                try {
                    state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState");
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                   throw new RuntimeException(e);
                }
                LOGGER.debug(("Browser state is: " + state));
            } while (!state.toString().equalsIgnoreCase("complete") && maxTime < System.currentTimeMillis());

            if (!state.toString().equalsIgnoreCase("complete")) {
                LOGGER.warn("Page not loaded in {} mms, state : {}", timeToWaitInMillis, state);
            }
        } else {
            LOGGER.warn("webDriver is not an instance of JavascriptExecutor!!");
        }
    }

    /**
     * waitForPageLoad and return the base from the current url. If the currentPage is "https://www.test.com:8080/test/page.html", will return :
     * "https://www.test.com" if webDriver is not instantiate, return empty string.
     * 
     * @return (String)
     */
    @Override
    public String getBaseURL() {

        String currentURL = getCurrentURL();
        try {
            URL url = new URL(currentURL);
            return new URL(currentURL).getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            LOGGER.error("The current Url returned by the browser is not valid [{}], O_o", currentURL, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get current url eg: "https://www.test.com:8080/test/page.html" if webDriver is not instantiate, return empty string.
     * 
     * @return (String)
     */
    @Override
    public String getCurrentURL() {
        waitForPageReady(20000);
        return webDriver.getCurrentUrl();
    }

    /**
     * Refresh web page
     */
    @Override
    public void refresh() {
        LOGGER.debug("Refreshing the browser");
        webDriver.navigate().refresh();
    }

    @Override
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    @Override
    public void scrollIntoView(WebElement webElement) {
        if (webDriver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", webElement);
        } else {
            LOGGER.warn("webDriver is not an instance of JavascriptExecutor!!");
        }
    }

    @Override
    public void switchToFrameAndWaitForElement(String frame, By locator) {
        webDriver.switchTo().frame(frame);
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

}
