package com.studyblue.qa.selenium.commons.v2.capabilities;

import java.util.List;

import com.studyblue.qa.selenium.commons.v2.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CapabilitiesSelectorImpl implements CapabilitiesSelector {

    private final List<CapabilitySupplier> capabilitySuppliers;

    public CapabilitiesSelectorImpl(List<CapabilitySupplier> capabilitySuppliers) {
        this.capabilitySuppliers = capabilitySuppliers;
    }

    /**
     * @see CapabilitiesSelector#getDesiredCapabilities(Browser)
     */
    @Override
    public DesiredCapabilities getDesiredCapabilities(Browser browser) {
        for (CapabilitySupplier capabilitySupplier : capabilitySuppliers) {
            if (capabilitySupplier.accept(browser)) {
                return capabilitySupplier.get();
            }
        }

        throw new IllegalArgumentException("Browser type " + browser.getName() + " not supported");
    }


}
