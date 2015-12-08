package com.studyblue.qa.selenium.commons.v2.customizer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class EventFiringWebDriverCustomizer implements WebDriverCustomizer {

    @Override
    public WebDriver customizeWebDriver(WebDriver webDriver) {
        return new EventFiringWebDriver(webDriver);
    }

}
