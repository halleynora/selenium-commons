package com.studyblue.qa.selenium.commons.v2.customizer;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Optional;

public class BrowserSizeWebDriverCustomizer implements WebDriverCustomizer{
    
    private final Optional<Dimension> browserSize;
    
    public BrowserSizeWebDriverCustomizer() {
        this(null);
    }
    
    public BrowserSizeWebDriverCustomizer(Dimension browserSize) {
        super();
        this.browserSize = Optional.fromNullable(browserSize);
    }

    @Override
    public WebDriver customizeWebDriver(WebDriver webDriver) {
        if(browserSize.isPresent()){
            webDriver.manage().window().setSize(browserSize.get());
        }else{
            webDriver.manage().window().maximize();
        }
        return webDriver;
    }

}
