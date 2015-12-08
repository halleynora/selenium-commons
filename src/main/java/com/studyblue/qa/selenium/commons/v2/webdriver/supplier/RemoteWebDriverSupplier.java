package com.studyblue.qa.selenium.commons.v2.webdriver.supplier;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

public class RemoteWebDriverSupplier implements Supplier<WebDriver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteWebDriverSupplier.class);

    private final DesiredCapabilities capability;
    private final URL hubUrl;
    private final Supplier<WebDriver> fallback;

    public RemoteWebDriverSupplier(DesiredCapabilities capability, URL hubUrl, Supplier<WebDriver> fallback) {
        this.capability = checkNotNull(capability, "DesiredCapabilities is mandatory");
        this.hubUrl = checkNotNull(hubUrl, "hubUrl is mandatory");
        this.fallback = checkNotNull(fallback, "fallback suplier is mandatory");
    }

    @Override
    public WebDriver get() {
        try {
            RemoteWebDriver webDriver = createWebDriver(hubUrl, capability);
            LOGGER.info("Remote web driver created on url [{}] with capabilities [{}]", hubUrl, capability);
            return webDriver;
        } catch (RuntimeException e) {
            LOGGER.warn("Unable to create a remoteWebDriver on url [{}] with capabilities [{}], will retry on next url", hubUrl, capability, e);
            return fallback.get();
        }
    }

    protected RemoteWebDriver createWebDriver(URL hubUrl, DesiredCapabilities capability) {
        return new RemoteWebDriver(hubUrl, capability);
    }
}
