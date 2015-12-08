package com.studyblue.qa.selenium.commons.v2.customizer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studyblue.qa.selenium.commons.v2.listener.DebugWebDriverEventListener;

public class DebugListenerDriverCustomizer implements WebDriverCustomizer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugListenerDriverCustomizer.class);

    @Override
    public WebDriver customizeWebDriver(WebDriver webDriver) {
        if (webDriver instanceof EventFiringWebDriver){
            ((EventFiringWebDriver) webDriver).register(new DebugWebDriverEventListener());
        }else{
            LOGGER.warn("webDriver[{}] is not an instance of EventFiringWebDriver, cannot attache the ExceptionWebDriverEventListener", webDriver);
        }
        
        return webDriver;
    }

}
