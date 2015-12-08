package com.studyblue.qa.selenium.commons.v2.customizer;

import static com.google.common.base.Preconditions.checkNotNull;

import com.studyblue.qa.selenium.commons.v2.listener.ExceptionWebDriverEventListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class ExceptionListenerWebDriverCustomizer implements WebDriverCustomizer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionListenerWebDriverCustomizer.class);
    
    private final Supplier<Scenario> scenarioSupplier;

    public ExceptionListenerWebDriverCustomizer(Supplier<Scenario> scenarioSupplier) {
        super();
        this.scenarioSupplier = checkNotNull(scenarioSupplier);
    }

    @Override
    public WebDriver customizeWebDriver(WebDriver webDriver) {
        
        if (webDriver instanceof EventFiringWebDriver){
            ((EventFiringWebDriver) webDriver).register(new ExceptionWebDriverEventListener(scenarioSupplier));
        }else{
            LOGGER.warn("webDriver[{}] is not an instance of EventFiringWebDriver, cannot atach the ExceptionWebDriverEventListener", webDriver);
        }
        
        return webDriver;
    }

}
