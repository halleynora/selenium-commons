package com.studyblue.qa.selenium.commons.v2.webdriver;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.studyblue.qa.selenium.commons.v2.BrowserDriverConfig;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelector;
import com.studyblue.qa.selenium.commons.v2.customizer.WebDriverCustomizer;

public class WebDriverFactorySuplier implements Supplier<WebDriverFactory> {
    
    private static final String LOCAL_GRIDHUB_URL = "http://localhost:4444/wd/hub";

    private final BrowserDriverConfig browserDriverConfig;
    private final CapabilitiesSelector capabilitiesSelector;
    private final List<WebDriverCustomizer> webDriverCustomizer;

    public WebDriverFactorySuplier(BrowserDriverConfig browserDriverConfig, CapabilitiesSelector capabilitiesSelector,
            List<WebDriverCustomizer> webDriverCustomizer) {
        super();
        this.browserDriverConfig = browserDriverConfig;
        this.capabilitiesSelector = capabilitiesSelector;
        this.webDriverCustomizer = webDriverCustomizer;
    }

    @Override
    public WebDriverFactory get() {
        switch (browserDriverConfig.getEnvironement()) {
        case grid:
            return new GridWebDriverFactory(browserDriverConfig, capabilitiesSelector, webDriverCustomizer);
        case localGrid:
            // overwrite the grid url 
            browserDriverConfig.setListOfGridHub(Collections.singletonList(LOCAL_GRIDHUB_URL));
            return new GridWebDriverFactory(browserDriverConfig, capabilitiesSelector, webDriverCustomizer);
        case local:
            return new LocalWebDriverFactory(browserDriverConfig, capabilitiesSelector, webDriverCustomizer);
        default:
            throw new IllegalArgumentException("Environement [" + browserDriverConfig.getEnvironement() + "] not supported");
        }
    }
}
