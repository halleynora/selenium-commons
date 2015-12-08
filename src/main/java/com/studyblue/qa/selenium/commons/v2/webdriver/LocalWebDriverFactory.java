package com.studyblue.qa.selenium.commons.v2.webdriver;

import java.util.List;

import com.studyblue.qa.selenium.commons.v2.BrowserDriverConfig;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.safari.SafariDriver;

import com.studyblue.qa.selenium.commons.v2.Browser;
import com.studyblue.qa.selenium.commons.v2.customizer.WebDriverCustomizer;

public class LocalWebDriverFactory extends BaseWebDriverFactory implements WebDriverFactory {

    public LocalWebDriverFactory(BrowserDriverConfig browserDriverConfig, CapabilitiesSelector capabilitiesSelector,
                                 List<WebDriverCustomizer> webDriverCustomizer) {
        super(browserDriverConfig, capabilitiesSelector, webDriverCustomizer);
    }

    @Override
    protected WebDriver createBrowser() {
        switch (getBrowserDriverConfig().getBrowser().getName()) {
        case Browser.CHROME:
            System.setProperty("webdriver.chrome.driver", getBrowserDriverConfig().getPathToChrome());
            return new ChromeDriver(getCapabilities());
        case Browser.FIREFOX:
            return new FirefoxDriver(getCapabilities());
        case Browser.PHANTOMJS:
            System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, getBrowserDriverConfig().getPathToPhantomjs());
            return new PhantomJSDriver(getCapabilities());
        case Browser.INTERNET_EXPLORER:
            return new InternetExplorerDriver(getCapabilities());
        case Browser.SAFARI:
            return new SafariDriver(getCapabilities());
        default:
            throw new IllegalStateException("Browser type " + getBrowserDriverConfig().getBrowser() + "not supported");
        }
    }
}
