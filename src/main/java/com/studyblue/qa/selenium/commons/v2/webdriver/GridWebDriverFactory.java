package com.studyblue.qa.selenium.commons.v2.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.studyblue.qa.selenium.commons.v2.BrowserDriverConfig;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelector;
import com.studyblue.qa.selenium.commons.v2.customizer.WebDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.webdriver.supplier.RemoteWebDriverSupplier;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.studyblue.qa.selenium.commons.v2.webdriver.supplier.RemoteWebDriverExceptionSupplier;

public class GridWebDriverFactory extends BaseWebDriverFactory implements WebDriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridWebDriverFactory.class);

    private final List<URL> listOfHub;

    public GridWebDriverFactory(BrowserDriverConfig browserDriverConfig, CapabilitiesSelector capabilitiesSelector,
                                List<WebDriverCustomizer> webDriverCustomizers) {
        super(browserDriverConfig, capabilitiesSelector, webDriverCustomizers);

        this.listOfHub = validateAndCleanHubList(browserDriverConfig.getListOfGridHub());
        LOGGER.info("GridFactory will be use with custom hub list: [{}].", listOfHub);
    }

    /**
     * This method test each hub and remove the bad hub definition
     * 
     * @param listOfHub (List<String>)
     * @return (List<String>)
     */
    private List<URL> validateAndCleanHubList(List<String> listOfHub) {
        List<URL> result = new ArrayList<URL>();
        for (String hub : listOfHub) {
            try {
                // add valid hub
                result.add(new URL(hub));
            } catch (MalformedURLException e) {
                LOGGER.warn("The hub [{}]  was reject of the hub list, because it's not valid.", hub);
            }
        }

        if (CollectionUtils.isEmpty(result)) {
            throw new IllegalArgumentException("List of hub contains no valid url, [" + listOfHub + "]");
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    protected WebDriver createBrowser() {
        
        Supplier<WebDriver> supplier = new RemoteWebDriverExceptionSupplier(listOfHub);
        for (URL url : Lists.reverse(listOfHub)) {
            supplier = new RemoteWebDriverSupplier(getCapabilities(), url, supplier);
        }
        
        return supplier.get();
    }

}
