package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import org.openqa.selenium.Platform;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.studyblue.qa.selenium.commons.v2.Browser;
import com.studyblue.qa.selenium.commons.v2.Browser.BrowserEnum;

public class PhantomJsCapabilitySupplier extends BaseCapabilitySupplier {

    @Override
    public boolean accept(Browser browser) {
        return BrowserEnum.phantomjs.getName().equals(browser.getName());
    }

    @Override
    public DesiredCapabilities get() {
        DesiredCapabilities capability = new DesiredCapabilities(BrowserType.CHROME, "", Platform.ANY);
        capability.setJavascriptEnabled(true);
        capability.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--web-security=no", "--ignore-ssl-errors=yes" });
        return addDefaultCapabilities(capability);
    }

}
