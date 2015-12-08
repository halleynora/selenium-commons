package com.studyblue.qa.selenium.commons.v2.capabilities.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitySupplier;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

public abstract class BaseCapabilitySupplier implements CapabilitySupplier {
    
    private static final String jenkinsBuildTag = System.getenv("BUILD_TAG");
    private static final boolean asJenkinsRun = StringUtils.isEmpty(jenkinsBuildTag);
    private static final String hostName = getHostName();
    
    private static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UNKNOWN_HOST";
        }
    }

        /**
     * Add the common capability to custom capabilities
     */
    protected DesiredCapabilities addDefaultCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability("takeScreenshot", true);
        desiredCapabilities.setCapability("hostName", hostName);
        // get the Jenkins build tag
        if (asJenkinsRun) {
            desiredCapabilities.setCapability("jenkinsBuildTag", jenkinsBuildTag);
        }
        return desiredCapabilities;
    }

}
