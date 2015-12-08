package com.studyblue.qa.selenium.commons;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Use to get an instance without init the grid, wehn we define driver in Spring. <br>
 * Cucumber.xml example: <br>
 * <bean id="driver" class="LazyDriver" depends-on="seleniumBrowserDriverBean"/><br>
 * <bean id="activityMonitor" class="com.studyblue.authoring.cucumber.activitymonitor.impl.ActivityMonitorImpl"><br>
 * <property name="executor" ref="driver" /><br>
 * </bean><br>
 * 
 *
 */
public class LazyDriver implements WebDriver, JavascriptExecutor {

    private WebDriver driver;

    @Override
    public void get(String url) {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        driver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.getPageSource();
    }

    @Override
    public void close() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        driver.close();
    }

    @Override
    public void quit() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.navigate();
    }

    @Override
    public Options manage() {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        return driver.manage();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        return jse.executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (driver == null) {
            driver = BrowserDriver.getCurrentDriver();
        }
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        return jse.executeAsyncScript(script, args);
    }

}
