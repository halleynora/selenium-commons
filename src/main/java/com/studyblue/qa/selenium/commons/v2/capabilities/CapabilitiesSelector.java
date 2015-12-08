package com.studyblue.qa.selenium.commons.v2.capabilities;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.studyblue.qa.selenium.commons.v2.Browser;

public interface CapabilitiesSelector {

    DesiredCapabilities getDesiredCapabilities(Browser browser);

}