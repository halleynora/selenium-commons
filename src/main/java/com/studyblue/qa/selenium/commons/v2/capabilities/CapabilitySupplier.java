package com.studyblue.qa.selenium.commons.v2.capabilities;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.base.Supplier;
import com.studyblue.qa.selenium.commons.v2.Browser;

public interface CapabilitySupplier extends Supplier<DesiredCapabilities> {
    
    boolean accept(Browser browser);

}
