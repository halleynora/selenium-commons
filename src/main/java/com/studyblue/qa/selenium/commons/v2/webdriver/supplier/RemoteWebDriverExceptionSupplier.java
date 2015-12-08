package com.studyblue.qa.selenium.commons.v2.webdriver.supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.join;

import java.net.URL;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Supplier;
import com.studyblue.qa.selenium.commons.v2.webdriver.WebDriverFactoryException;

public class RemoteWebDriverExceptionSupplier implements Supplier<WebDriver> {
    private final List<URL> listOfHub;

    public RemoteWebDriverExceptionSupplier(List<URL> listOfHub) {
        this.listOfHub = checkNotNull(listOfHub, "listOfHub is mandatory");
    }

    @Override
    public RemoteWebDriver get() {
        throw new WebDriverFactoryException("Unable to init the Webdriver with the grid hub: [" + join(listOfHub, ",") + "], see log for other exception");
    }

}
