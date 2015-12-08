package com.studyblue.qa.selenium.commons.v2.webdriver;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.studyblue.qa.selenium.commons.v2.BrowserDriverConfig;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelector;
import com.studyblue.qa.selenium.commons.v2.customizer.WebDriverCustomizer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseWebDriverFactory implements WebDriverFactory {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final BrowserDriverConfig browserDriverConfig;
    private final CapabilitiesSelector capabilitiesSelector;
    private final List<WebDriverCustomizer> webDriverCustomizers;

    public BaseWebDriverFactory(BrowserDriverConfig browserDriverConfig, CapabilitiesSelector capabilitiesSelector,
            List<WebDriverCustomizer> webDriverCustomizers) {
        super();
        this.browserDriverConfig = checkNotNull(browserDriverConfig);
        this.capabilitiesSelector = checkNotNull(capabilitiesSelector);
        this.webDriverCustomizers = checkNotNull(webDriverCustomizers);
    }

    @Override
    public WebDriver getWebDriver() {
        WebDriver webDriver = null;
        try {
            webDriver = createBrowser();

            webDriver = applyCustomizer(webDriver);
        } catch (RuntimeException e) {
            LOGGER.error("Error initilizing the webdriver, trying to close brower if any", e);
            quitBrowser(webDriver);
            throw e;
        }

        return webDriver;

    }

    private void quitBrowser(WebDriver webDriver) {
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (RuntimeException e) {
                LOGGER.error("Error while trying to quit the browser, you could have a remainning open broweser", e);
            }
        }
    }

    protected WebDriver applyCustomizer(WebDriver webDriver) {

        for (WebDriverCustomizer webDriverCustomizer : webDriverCustomizers) {
            webDriver = webDriverCustomizer.customizeWebDriver(webDriver);
        }
        return webDriver;
    }

    protected abstract WebDriver createBrowser();

    protected DesiredCapabilities getCapabilities() {
        return capabilitiesSelector.getDesiredCapabilities(browserDriverConfig.getBrowser());
    }

    public BrowserDriverConfig getBrowserDriverConfig() {
        return browserDriverConfig;
    }

    public CapabilitiesSelector getCapabilitiesSelector() {
        return capabilitiesSelector;
    }

    public List<WebDriverCustomizer> getWebDriverCustomizers() {
        return webDriverCustomizers;
    }

}