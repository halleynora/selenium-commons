package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.studyblue.qa.selenium.commons.v2.Browser;
import com.studyblue.qa.selenium.commons.v2.Browser.BrowserEnum;

public class SafariCapabilitySupplier extends BaseCapabilitySupplier {

    @Override
    public boolean accept(Browser browser) {
        return BrowserEnum.safari.getName().equals(browser.getName());
    }

    @Override
    public DesiredCapabilities get() {
        DesiredCapabilities capability = DesiredCapabilities.safari();
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return addDefaultCapabilities(capability);
    }

}
