package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.studyblue.qa.selenium.commons.v2.Browser;
import com.studyblue.qa.selenium.commons.v2.Browser.BrowserEnum;

public class ExplorerCapabilitySupplier extends BaseCapabilitySupplier {

    @Override
    public boolean accept(Browser browser) {
        return BrowserEnum.internetexplorer.getName().equals(browser.getName());
    }

    @Override
    public DesiredCapabilities get() {
        DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
        capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        return addDefaultCapabilities(capability);
    }

}
