package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import java.io.File;

import com.studyblue.qa.selenium.commons.v2.Browser;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxCapabilitySupplier extends BaseCapabilitySupplier {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxCapabilitySupplier.class);

    @Override
    public boolean accept(Browser browser) {
        return Browser.BrowserEnum.firefox.getName().equals(browser.getName());
    }

    @Override
    public DesiredCapabilities get() {
        FirefoxProfile profile = new FirefoxProfile();
        DesiredCapabilities capability = DesiredCapabilities.firefox();

        LOGGER.info("Initiating Firefox grid");
        File file = new File(System.getProperty("user.dir"));
        LOGGER.info("Setting download destination: " + file);
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", file.getParent().toString());
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
        profile.setPreference("security.mixed_content.block_active_content", false);

        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); // TODO DPA move to default capability
        capability.setCapability(FirefoxDriver.PROFILE, profile);
        return addDefaultCapabilities(capability);
    }

}
