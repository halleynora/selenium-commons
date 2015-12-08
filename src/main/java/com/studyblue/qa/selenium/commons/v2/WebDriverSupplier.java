package com.studyblue.qa.selenium.commons.v2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.PostConstruct;

import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelector;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.capabilities.impl.ExplorerCapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.capabilities.impl.PhantomJsCapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.capabilities.impl.SafariCapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.customizer.BrowserSizeWebDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.customizer.DebugListenerDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.customizer.EventFiringWebDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.customizer.WebDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.webdriver.WebDriverFactorySuplier;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.studyblue.qa.selenium.commons.v2.capabilities.CapabilitiesSelectorImpl;
import com.studyblue.qa.selenium.commons.v2.capabilities.impl.ChromeCapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.capabilities.impl.FirefoxCapabilitySupplier;
import com.studyblue.qa.selenium.commons.v2.customizer.ExceptionListenerWebDriverCustomizer;
import com.studyblue.qa.selenium.commons.v2.webdriver.WebDriverFactory;

import cucumber.api.Scenario;

public class WebDriverSupplier implements Supplier<WebDriver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverSupplier.class);

    public Supplier<Scenario> scenarioSupplier; // Use by screenShot to name the png file with the scenario name
    private CapabilitiesSelector capabilitiesSelector;

    private final BrowserDriverConfig browserDriverConfig;
    private Supplier<WebDriverFactory> webDriverFactorySupplier;
    private List<WebDriverCustomizer> webDriverCustomizer;
    private WebDriverFactory webDriverFactory;

    public WebDriverSupplier(BrowserDriverConfig browserDriverConfig) {
        this(browserDriverConfig, null);
    }

    public WebDriverSupplier(BrowserDriverConfig browserDriverConfig, Supplier<Scenario> scenarioSupplier) {
        this.browserDriverConfig = checkNotNull(browserDriverConfig);
        this.scenarioSupplier = scenarioSupplier;
    }

    @Override
    public WebDriver get() {
        return webDriverFactory.getWebDriver();
    }

    @PostConstruct
    public void init() {
        webDriverFactory = getWebDriverFactorySupplier().get();
    }

    public CapabilitiesSelector getCapabilitiesSelector() {
        if (capabilitiesSelector == null) {
            capabilitiesSelector = new CapabilitiesSelectorImpl(Lists.newArrayList( //
                    (CapabilitySupplier) new ChromeCapabilitySupplier(), //
                    (CapabilitySupplier) new FirefoxCapabilitySupplier(), //
                    (CapabilitySupplier) new SafariCapabilitySupplier(), //
                    (CapabilitySupplier) new ExplorerCapabilitySupplier(), //
                    (CapabilitySupplier) new PhantomJsCapabilitySupplier() //
            ));
        }
        return capabilitiesSelector;
    }

    public void setCapabilitiesSelector(CapabilitiesSelector capabilitiesSelector) {
        this.capabilitiesSelector = capabilitiesSelector;
    }

    public Supplier<WebDriverFactory> getWebDriverFactorySupplier() {
        if (webDriverFactorySupplier == null) {
            webDriverFactorySupplier = new WebDriverFactorySuplier(getBrowserDriverConfig(), getCapabilitiesSelector(), getWebDriverCustomizer());
        }
        return webDriverFactorySupplier;
    }

    public void setWebDriverFactorySupplier(Supplier<WebDriverFactory> webDriverFactorySupplier) {
        this.webDriverFactorySupplier = webDriverFactorySupplier;
    }

    public List<WebDriverCustomizer> getWebDriverCustomizer() {
        if (webDriverCustomizer == null) {
            LOGGER.debug("webDriverCustomizer was null, will add the default one, [{}, {}, {}, {}]", EventFiringWebDriverCustomizer.class.getSimpleName(),
                    DebugListenerDriverCustomizer.class.getSimpleName(), ExceptionListenerWebDriverCustomizer.class.getSimpleName(),
                    BrowserSizeWebDriverCustomizer.class.getSimpleName());
            webDriverCustomizer = Lists.newArrayList(new EventFiringWebDriverCustomizer(), //
                    new DebugListenerDriverCustomizer(), //
                    new BrowserSizeWebDriverCustomizer(getBrowserDriverConfig().getBrowerSize())); //
            if (scenarioSupplier != null) {
                webDriverCustomizer.add(new ExceptionListenerWebDriverCustomizer(scenarioSupplier));
            }
        }
        return webDriverCustomizer;
    }

    public void setWebDriverCustomizer(List<WebDriverCustomizer> webDriverCustomizer) {
        this.webDriverCustomizer = webDriverCustomizer;
    }

    public Supplier<Scenario> getScenarioSupplier() {
        return scenarioSupplier;
    }

    public BrowserDriverConfig getBrowserDriverConfig() {
        return browserDriverConfig;
    }

}