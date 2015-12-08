package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import com.studyblue.qa.selenium.commons.v2.Browser;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeCapabilitySupplier extends BaseCapabilitySupplier {

    @Override
    public boolean accept(Browser browser) {
        return Browser.BrowserEnum.chrome.getName().equals(browser.getName());
    }

    @Override
    public DesiredCapabilities get() {
        DesiredCapabilities capability = DesiredCapabilities.chrome();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("allow-running-insecure-content");
        options.addArguments("--start-maximized");

        capability.setCapability(ChromeOptions.CAPABILITY, options);
        return addDefaultCapabilities(capability);
    }

}
