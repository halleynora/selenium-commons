package com.studyblue.qa.selenium.commons.v2;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;

import com.google.common.collect.Lists;

public class BrowserDriverConfig {

    public static BrowserDriverConfigBuilder builder() {
        return new BrowserDriverConfigBuilder();
    }

    private static final String HUB_URL_PRIMARY = "http://10.66.110.19:80/wd/hub"; // BETA_GRID QALAB
    private static final String HUB_URL_SECONDARY = "http://10.1.200.233:4444/wd/hub"; // DC_GRID

    private Browser browser = Browser.BrowserEnum.chrome;
    private Environement environement = Environement.local;
    private long timeoutInSeconds = 60;
    private List<String> listOfGridHub = Lists.newArrayList(HUB_URL_PRIMARY, HUB_URL_SECONDARY); // list of hub to customize grid fallback
    private boolean includeHtmlSourceOnError = false; // QUALENG-42 (DPA) to add in scenario the the htlmsource of the browser when an exception occurs.

    private String pathToChrome; // The path of the executable of chrome (local only)
    private String pathToPhantomjs; // PHANTOMJS_EXECUTABLE_PATH_PROPERTY (local only)
    private Boolean firefoxDebugFlag = false; // Use to debug with firefox. Set firebug and firepath plugin and keep browser open on error. (local only)
    private String pathToFirebug; // Use with firefoxDebugFlag to set pathToFirebug (local only)
    private String pathToFirepath; // Use with firefoxDebugFlag to set pathToFirepath (local only)
    private Dimension browerSize;

    public String getPathToChrome() {
        return pathToChrome;
    }

    public void setPathToChrome(String pathToChrome) {
        this.pathToChrome = pathToChrome;
    }

    public String getPathToPhantomjs() {
        return pathToPhantomjs;
    }

    public void setPathToPhantomjs(String pathToPhantomjs) {
        this.pathToPhantomjs = pathToPhantomjs;
    }

    public Boolean getFirefoxDebugFlag() {
        return firefoxDebugFlag;
    }

    public void setFirefoxDebugFlag(Boolean firefoxDebugFlag) {
        this.firefoxDebugFlag = firefoxDebugFlag;
    }

    public String getPathToFirebug() {
        return pathToFirebug;
    }

    public void setPathToFirebug(String pathToFirebug) {
        this.pathToFirebug = pathToFirebug;
    }

    public String getPathToFirepath() {
        return pathToFirepath;
    }

    public void setPathToFirepath(String pathToFirepath) {
        this.pathToFirepath = pathToFirepath;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }
    
    public void setBrowserString(final String browser) {
        checkArgument(StringUtils.isNotBlank(browser));
        this.browser = new Browser(){
            @Override
            public String getName() {
                return browser;
            }};
    }

    public Environement getEnvironement() {
        return environement;
    }

    public void setEnvironement(Environement environement) {
        this.environement = environement;
    }

    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public List<String> getListOfGridHub() {
        return listOfGridHub;
    }

    public void setListOfGridHub(List<String> listOfGridHub) {
        this.listOfGridHub = listOfGridHub;
    }

    public boolean isIncludeHtmlSourceOnError() {
        return includeHtmlSourceOnError;
    }

    public void setIncludeHtmlSourceOnError(boolean includeHtmlSourceOnError) {
        this.includeHtmlSourceOnError = includeHtmlSourceOnError;
    }

    public Dimension getBrowerSize() {
        return browerSize;
    }

    public void setBrowerSize(Dimension browerSize) {
        this.browerSize = browerSize;
    }

    public static class BrowserDriverConfigBuilder {
        BrowserDriverConfigBuilder() {
        }

        private BrowserDriverConfig instance;

        protected BrowserDriverConfig getInstance() {
            return instance;
        }

        public BrowserDriverConfigBuilder withPathToChrome(String aValue) {
            instance.setPathToChrome(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withPathToPhantomjs(String aValue) {
            instance.setPathToPhantomjs(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withFirefoxDebugFlag(Boolean aValue) {
            instance.setFirefoxDebugFlag(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withPathToFirebug(String aValue) {
            instance.setPathToFirebug(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withPathToFirepath(String aValue) {
            instance.setPathToFirepath(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withBrowser(Browser aValue) {
            instance.setBrowser(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withEnvironement(Environement aValue) {
            instance.setEnvironement(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withTimeoutInSeconds(long aValue) {
            instance.setTimeoutInSeconds(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withListOfGridHub(List<String> aValue) {
            instance.setListOfGridHub(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withAddedListOfGridHubElement(String aValue) {
            if (instance.getListOfGridHub() == null) {
                instance.setListOfGridHub(new ArrayList<String>());
            }

            instance.getListOfGridHub().add(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withIncludeHtmlSourceOnError(boolean aValue) {
            instance.setIncludeHtmlSourceOnError(aValue);
            return this;
        }

        public BrowserDriverConfigBuilder withBrowerSize(Dimension aValue) {
            instance.setBrowerSize(aValue);
            return this;
        }

        public BrowserDriverConfig build() {
            return getInstance();
        }

    }
}
