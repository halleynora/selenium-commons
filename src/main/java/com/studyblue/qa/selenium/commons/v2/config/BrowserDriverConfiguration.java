package com.studyblue.qa.selenium.commons.v2.config;

import com.studyblue.qa.selenium.commons.BrowserDriver;
import com.studyblue.qa.selenium.commons.v2.BrowserDriverConfig;
import com.studyblue.qa.selenium.commons.v2.WebDriverSupplier;
import com.studyblue.qa.selenium.commons.v2.bean.ScenarioSupplier;
import com.studyblue.qa.selenium.commons.v2.bean.ScenarioSupplierImpl;
import com.studyblue.qa.selenium.commons.v2.bean.ScenarioWrapper;
import com.studyblue.qa.selenium.commons.v2.helper.SeleniumHelperFactory;
import com.studyblue.qa.selenium.commons.v2.helper.impl.SeleniumHelperFactoryImpl;
import com.studyblue.qa.selenium.commons.v2.helper.impl.SeleniumSupportFactoryImpl;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.studyblue.qa.selenium.commons.v2.helper.SeleniumSupport;

import cucumber.runtime.java.spring.CucumberSpringConfig;

@Configuration
public class BrowserDriverConfiguration implements InitializingBean {

    @Autowired
    private BrowserDriverConfig browserDriverConfig;

    @Bean(name = "webDriverSupplier")
    public Supplier<WebDriver> getWebDriverSupplier(WebDriver webDriver) {
        return Suppliers.ofInstance(webDriver);
    }

    @Bean(name = "scenarioSupplier")
    @Scope(value = CucumberSpringConfig.GLUE_SCOPE_NAME, proxyMode = ScopedProxyMode.INTERFACES)
    public ScenarioSupplier getScenarioSupplier() {
        return new ScenarioSupplierImpl();
    }

    @Bean(name = "webDriverSupplier")
    public WebDriverSupplier getWebDriverSupplier() {
        return new WebDriverSupplier(browserDriverConfig);
    }

    @Bean(name = "webDriver")
    public WebDriver getWebDriver() {
        return getWebDriverSupplier().get();
    }

    @Bean(name = "seleniumHelperFactory")
    public SeleniumHelperFactory getSeleniumHelperFactory() {
        return new SeleniumHelperFactoryImpl(getWebDriver(), getScenarioSupplier());
    }

    @Bean(name = "seleniumSupportFactory")
    public SeleniumSupport getSeleniumSupportFactory() {
        return new SeleniumSupportFactoryImpl(getWebDriver());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // backward compatible
        BrowserDriver.mDriver = getWebDriver();
        BrowserDriver.scenario = new ScenarioWrapper(getScenarioSupplier());

    }
    
    protected BrowserDriverConfig getBrowserDriverConfig() {
        return browserDriverConfig;
    }

    protected void setBrowserDriverConfig(BrowserDriverConfig browserDriverConfig) {
        this.browserDriverConfig = browserDriverConfig;
    }

}
