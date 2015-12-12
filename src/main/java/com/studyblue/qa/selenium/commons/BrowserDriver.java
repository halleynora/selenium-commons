package com.studyblue.qa.selenium.commons;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.Scenario;

public class BrowserDriver {
    // QUALENG-101 (mremillard): Defining a "debug mode" flag. When it is false the getWebElementAndLocator() method will
    // never call method getWebElementLocator(). In so doing it will reduce the network traffic a little.
    private static boolean inDebugMode = false;

    public static WebDriver mDriver; // The handle of WebDriver

    // ---Spring variables
    public static String whereToRun; // Where the browser will open. Possible values: local, grid
                                     // and localgrid
    public static String browserName; // Which browser will be use. Possible values: firefox,
                                      // chrome, phantomjs(local only) and internetexplorer
    // public static String gridUrl = "http://10.1.210.77:4444/wd/hub"; // Default grid url
    private static List<String> listOfGridHub; // list of hub to customize grid fallback
    private static boolean includeVideoLink = false; // To add in the cucumber repport the video
                                                     // link (Use only with gridExtras)
                                                     // QUALENG-23 (DPA) 23/01/2015
    private static boolean includeHtmlSourceOnError = false; // QUALENG-42 (DPA) to add in scenario
                                                             // the the htlmsource of the browser
                                                             // when an exception occurs.
    // for local
    public static String pathToChrome; // The path of the executable of chrome (local only)
    public static String pathToPhantomjs; // PHANTOMJS_EXECUTABLE_PATH_PROPERTY (local only)
    public static Boolean firefoxDebugFlag = false; // Use to debug with firefox. Set firebug and
                                                    // firepath plugin and keep browser open on
                                                    // error. (local only)
    public static String pathToFirebug; // Use with firefoxDebugFlag to set pathToFirebug (local
                                        // only)
    public static String pathToFirepath; // Use with firefoxDebugFlag to set pathToFirepath (local
                                         // only)

    public static Scenario scenario; // Use by screenShot to name the png file with the scenario
                                     // name
    private static long timeoutInSeconds = 60; // Default timeout for BrowserDriver actions

    private static int shotNumber = 1; // Use by screenShot
    private static int sourceNumber = 1; // Use by htmlSource
    private static final Logger log = LoggerFactory.getLogger(BrowserDriver.class);
    private static GridFactory gridFactory;

    // QUALENG-94 (mremillard): Implemented this class so that we could have an object which contained both a WebElement
    // and its associated "locator".
    private static class WebElementAndLocator {
        public WebElement webElement = null;
        public String locator = null;

        public WebElementAndLocator() {
        }
    }

    public BrowserDriver() {
        log.info("BrowserDriver constructor invoked");
    }

    /**
     * Instantiate driver or return one if it exists
     * 
     * @return Driver object to take action on
     * @throws Exception
     */
    public synchronized static WebDriver getCurrentDriver() {
        if (mDriver == null) {
            initDriver();

            if (mDriver == null) {
                throw new RuntimeException("Browser Driver Initialization failed..");
            }
        }

        return mDriver;
    }

    public static Boolean isDriverNull() {
        return (mDriver == null);
    }

    private static void initDriver() {
        try {
            log.info("Initializing " + browserName + " browser @" + whereToRun + " ..");
            if (whereToRun.equals("local")) {
                initLocal();
            } else if (whereToRun.equals("grid")) {
                String overrideGridHubList = System.getenv("OVERRIDE_GRID_HUB_LIST");
                if (overrideGridHubList != null && !overrideGridHubList.isEmpty()) {
                    log.info("Environnement variable: OVERRIDE_GRID_HUB_LIST was detected.");
                    log.info("Override the grid hub list with: [" + overrideGridHubList + "]");
                    setListOfGridHub(overrideGridHubList);
                }

                if (BrowserDriver.listOfGridHub == null || BrowserDriver.listOfGridHub.isEmpty()) {
                    // init grid factory with local hub
                    gridFactory = new GridFactory();
                } else {
                    // init grid factory with custom list of hub
                    gridFactory = new GridFactory(BrowserDriver.listOfGridHub);
                }

                initOnGrid(gridFactory);
            } else if (whereToRun.equals("localgrid")) {
                // init grid factory with local hub
                gridFactory = new GridFactory(Arrays.asList("http://192.168.99.100:32774/wd/hub"));
                initOnGrid(gridFactory);
//                initLocalGrid();
            }

            // Maximize a browser window
            if (mDriver != null) {
                mDriver.manage().window().maximize();
            }
        } catch (Exception e) {
            log.error("Browser initialization issue (" + mDriver + ")", e);
            if (mDriver != null) {
                try {
                    mDriver.quit();
                    mDriver = null;
                } catch (Exception e2) {
                    log.info("browser initialization issue recovery failed: cannot cleanup (" + mDriver + ")", e2);
                }
            }

            handleException(e, null);
        } finally {
            if (mDriver != null) {
                Runtime.getRuntime().addShutdownHook(new Thread(new BrowserCleanup()));
                log.info("Browser initialized and shutdownHook added: " + mDriver);
            }
        }
    }

    private static void initLocal() throws Exception {
        if (browserName.equals("firefox")) {
            log.info("Initiating local Firefox.");
            FirefoxProfile profile = new FirefoxProfile();
            File file = new File(System.getProperty("user.dir"));
            log.info("Setting download destination: " + file);
            if (firefoxDebugFlag) {
                profile.addExtension(new File(pathToFirebug));
                profile.addExtension(new File(pathToFirepath));
                profile.setPreference("extensions.firebug.currentVersion", "2.0.1");
                profile.setPreference("extensions.firebug.onByDefault", true);
                profile.setPreference("extensions.firebug.defaultPanelName", "net");
                profile.setPreference("extensions.firebug.net.enableSites", true);
            }
            profile.setPreference("security.mixed_content.block_active_content", false);
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.dir", file.getParent().toString());
            // profile.setPreference("browser.helperApps.neverAsk.saveToDisk","image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");

            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;" + "application/pdf;"
                            + "application/vnd.openxmlformats-officedocument.wordprocessingml.document;" + "text/plain;" + "text/csv;"
                            + "application/vnd.ms-excel;" + "application/x-excel;" + "application/x-msexcel;" + "application/excel;");
            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            cap.setCapability("firefox_profile", profile);
            mDriver = new FirefoxDriver(cap);
        } else if (browserName.equals("chrome")) {
            System.setProperty("webdriver.chrome.driver", pathToChrome);
            ChromeOptions o = new ChromeOptions();
            o.addArguments("allow-running-insecure-content");
            mDriver = new ChromeDriver(o);
        } else if (browserName.equalsIgnoreCase("phantomjs")) {
            System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathToPhantomjs);
            DesiredCapabilities capabilities = new DesiredCapabilities(BrowserType.CHROME, "", Platform.ANY);
            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--web-security=no", "--ignore-ssl-errors=yes" });
            mDriver = new PhantomJSDriver(capabilities);
        } else {
            mDriver = new InternetExplorerDriver();
        }
    }


    private static void initOnGrid(GridFactory pGridFactory) throws GridFactoryException {
        if (browserName.equals("firefox")) {
            log.info("Initiating Firefox grid");
            FirefoxProfile profile = new FirefoxProfile();
            File file = new File(System.getProperty("user.dir"));
            ;
            log.info("Setting download destination: " + file);
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.dir", file.getParent().toString());
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
            profile.setPreference("security.mixed_content.block_active_content", false);
            mDriver = pGridFactory.getFirefoxInstance(profile);
        } else if (browserName.equals("chrome")) {
            ChromeOptions o = new ChromeOptions();
            o.addArguments("allow-running-insecure-content");
            mDriver = pGridFactory.getChromeInstance(o);
        } else if (browserName.equalsIgnoreCase("phantomjs")) {
            throw new RuntimeException("phantomjs support is mainly for local dev; may not be supported by the grid..");
        } else if (browserName.equalsIgnoreCase("safari")) {
            // QUALENG-58
            mDriver = pGridFactory.getSafariInstance();
        } else {
            mDriver = pGridFactory.getInternetExplorerInstance();
        }

        if (mDriver != null) {
            mDriver = new Augmenter().augment(mDriver);

            // Embed video on the bdd scenario when the test run on grid
            if (scenario != null) {
                // QUALENG-23 (DPA) 26/01/2015 print grid info
                scenarioPrintGridInfo(scenario);
                if (isIncludeVideoLink()) // QUALENG-23 (DPA) 23/01/2015
                {
                    // Eric Frenette Christmas gift b
                    scenarioPrintVideo(scenario);
                }

            }
        }
    }

    public static void browserChange(String newDriverName) {
        if (mDriver == null) {
            browserName = newDriverName;
        } else if (!newDriverName.equalsIgnoreCase(browserName)) {
            browserName = newDriverName;
            close();
        }
    } // browserChange()

    /**
     * Load a page and validate the title for the page returned
     * 
     * @param url URL to load
     * @param title Title of page that is loaded
     */
    public static void loadPage(String url, final String title) {
        loadPage(url);

        try {
            (new WebDriverWait(mDriver, 30)).until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver d) {
                    return d.getTitle().startsWith(title);
                }
            });
            assertEquals(title, mDriver.getTitle());

        } catch (Exception e) {
            handleException(e, "loadPage");
        }
    } // loadPage()

    /**
     * Load a page without validation
     * 
     * @param url (String) URL to load
     */
    public static void loadPage(String url) {
        log.info("Directing browser to: " + url);

        try {
            scenarioPrintText("Opening " + browserName + " with: <a href='" + url + "'>" + url + "</a>");
            getCurrentDriver().get(url);
        } catch (Exception e) {
            handleException(e, "loadPage");
        }
    } // loadPage()

    // QUALENG-DPA
    public static void navigateTo(String url) {
        log.info("Navigate browser to: " + url);

        try {
            scenarioPrintText("Navigate to : <a href='" + url + "'>" + url + "</a>");
            BrowserDriver.getCurrentDriver().navigate().to(url);
        } catch (Exception e) {
            handleException(e, "navigateTo");
        }
    } // navigate()

    /**
     * wait a max of 20 seconds for page load. Until document.readyState = complete
     */
    public static void waitForPageLoad() {
        String state = "NA";
        int counter = 0;
        getCurrentDriver();
        if (mDriver != null) {
            do {
                try {
                    state = (String) ((JavascriptExecutor) mDriver).executeScript("return document.readyState");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    handleException(e, "waitForPageLoad");
                }
                counter++;
                log.info(("Browser state is: " + state));
            } while (!state.equalsIgnoreCase("complete") && counter < 20);
        }
    }

    /**
     * waitForPageLoad and return the base from the current url. If the currentPage is "https://www.test.com:8080/test/page.html", will return :
     * "https://www.test.com" if mDriver is not instantiate, return empty string.
     * 
     * @return (String)
     */
    public static String getBaseURL() {

        String currentURL = getCurrentURL();
        String protocol = null;
        String domain = null;
        if (currentURL.isEmpty()) {
            log.error("getBaseURL(): WebDriver is not instantiated!");
            return currentURL;
        }
        try {
            URL url = new URL(currentURL);
            protocol = url.getProtocol();
            domain = url.getHost();
        } catch (MalformedURLException e) {
            handleException(e, "getBaseURL");
        }

        return protocol + "://" + domain;
    } // getBaseURL()

    /**
     * Get current url eg: "https://www.test.com:8080/test/page.html" if mDriver is not instantiate, return empty string.
     * 
     * @return (String)
     */
    public static String getCurrentURL() {
        // (DPA) Validating that mDriver is defined.
        if (mDriver != null) {
            BrowserDriver.waitForPageLoad();
            return mDriver.getCurrentUrl();
        } else {
            log.error("getCurrentURL(); WebDriver is not instantiated!");
            return "";
        }
    } // getCurrentURL()

    /**
     * Refresh web page
     */
    public static void refresh() {
        log.info("Refreshing the browser");
        try {
            getCurrentDriver().navigate().refresh();
        } catch (Exception e) {
            handleException(e, "refresh");
        }
    }

    /**
     * Take a screen shot.<br>
     * <b>!!!Attention</b> : if you want your snapshots to be attached to cucumber scenario you <b>obligatory</b> need to add this code either to any descendant
     * of a class with @ContextConfiguration annotation or to the class itself if it doesn't have any descendants <br>
     * 
     * Code to add :
     * 
     * <pre>
     *   \@Before
     *  public void prepareTest(Scenario scenario) { 
     *      if (scenario != null) { 
     *      BrowserDriver.scenario = scenario;
     *      } 
     *   }
     * </pre>
     * 
     * @param text : name file to use
     */
    public static void screenShot(String text) {
        try {
            if (mDriver == null) {
                log.info("Failed to take a snapshot because Driver was NULL.");
                return;
            }
            if (scenario == null) {
                File srcFile = ((TakesScreenshot) mDriver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(srcFile, new File("./target/" + shotNumber + "_" + text + ".png"));
                shotNumber++;
                log.error("Scenario is NULL while taking screenshot!!!");
            } else {
                byte[] srcBytes = ((TakesScreenshot) mDriver).getScreenshotAs(OutputType.BYTES);
                scenario.embed(srcBytes, "image/png");
            }
        } catch (Exception e) {
            log.error("Exception while taking snapshot: ", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    // REF: QUALENG-42.
    /**
     * Takes a HTML source of the current browser or frame.<br>
     * 
     * <b>Mandatory</b> to add this code either to any descendant of a class with @ContextConfiguration annotation or to the class itself if it doesn't have any
     * descendants <br>
     * 
     * @return (String) The path of the source file.
     */
    public static String getPageSource() {
        String result = null;

        try {
            if (mDriver == null) {
                log.info("Failed to retrieve the HTML source because the WebDriver was NULL.");
            } else {
                String source = mDriver.getPageSource();
                String unicId = UUID.randomUUID().toString();

                File sourceFile = new File("./target/results/pageSource-" + unicId + ".txt");
                try {
                    Writer output = new BufferedWriter(new FileWriter(sourceFile));
                    output.write(source);
                    output.close();
                    result = sourceFile.getCanonicalPath();
                } catch (IOException e) {
                    log.error("Problem writing HTML source. " + sourceFile.getPath(), e);
                }

                if (scenario != null) {
                    // Inserting a link with the HTML source file into the scenario.
                    String html = "HTML source: <a href=\"" + sourceFile.getCanonicalPath() + "\">Html source</a><br>";
                    scenario.write(html);
                } else {
                    log.info("HTML source: " + sourceFile.getCanonicalPath());
                }
            }
        } catch (Exception e) {
            log.error("Exception while retrieving HTML page source: ", ExceptionUtils.getRootCauseMessage(e));
        }

        return result;
    } // getPageSource()

    /**
     * Take a html source of the current browser or frame<br>
     * <b>Mandatory</b> to add this code either to any descendant of a class with @ContextConfiguration annotation or to the class itself if it doesn't have any
     * descendants <br>
     * 
     * Code to add :
     * 
     * <pre>
     *   \@Before
     *  public void prepareTest(Scenario scenario) { 
     *      if (scenario != null) { 
     *      BrowserDriver.scenario = scenario;
     *      } 
     *   }
     * </pre>
     * 
     * @param pSuffix (String)
     * @deprecated Use getPageSource()
     */
    @Deprecated
    public static void htmlSource(String text) {
        try {
            if (mDriver == null) {
                log.error("Failed to get the HTML source because Driver was NULL.");
                return;
            }
            String htmlSrc = mDriver.getPageSource();
            if (htmlSrc != null && !htmlSrc.isEmpty()) {
                String fileName = "./target/source" + sourceNumber + "_" + text + ".txt";
                File file = new File(fileName);
                FileUtils.writeStringToFile(file, htmlSrc);
                sourceNumber++;
                if (scenario == null) {
                    log.info("See html source: " + fileName);
                } else {
                    scenario.write("See html source: <a href='" + fileName + "'>" + fileName + "</a>");
                }
            }
        } catch (Exception e) {
            log.error("Exception while taking snapshot: ", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * Print in the scenario the link to see video (Available only with GridExtra) <b>Mandatory</b> add this code either to any descendant of a class
     * with @ContextConfiguration annotation or to the class itself if it doesn't have any descendants <br>
     * 
     * Code to add :
     * 
     * <pre>
     *   \@Before
     *  public void prepareTest(Scenario scenario) { 
     *      if (scenario != null) { 
     *      BrowserDriver.scenario = scenario;
     *      } 
     *   }
     * </pre>
     * 
     * @param scenario (Scenario)
     */
    private static void scenarioPrintVideo(Scenario scenario) {
        String mp4Url = getVideoUrl();

        if (mp4Url != null) {
            String title = "Grid session id: " + getSessionId();
            String embedHtml = "<a target='_blank' title='" + title + "' href='" + mp4Url + "'>Video Link (may take a few seconds to complete encoding)</a>";
            scenario.write(embedHtml);
        }
    }

    /**
     * Embed in the cucumber scenario the Grid hub, the grid info Hub, node, session-id and capability
     * 
     * @param scenario
     */
    public static void scenarioPrintGridInfo(Scenario scenario) {
        if (gridFactory != null) {
            String hub = "<b>Hub:</b> <span id=hub>" + gridFactory.getCurrentHub() + "</span><br>";
            String nodeIp = "<b>Node:</b> ";
            String sessionInfo = "<b>Session id:</b> <span id=sessionid>" + getSessionId() + "</span><br>";
            String capability = "<b>Capabilities:</b> <span id=capabilities>" + getCapabilities() + "</span><br>";
            if (gridFactory != null) {
                nodeIp = "<b>Node:</b> <span id=node>" + gridFactory.getNodeIp(getSessionId()) + "</span><br>";
            }
            scenarioPrintToggleText("Grid info:", hub + nodeIp + sessionInfo + capability);
        }

    }

    /**
     * Embed in scenario a pTitle clickable to toggle a ptext. <b>!!!Attention</b> : Mandatory add this code either to any descendant of a class
     * with @ContextConfiguration annotation or to the class itself if it doesn't have any descendants <br>
     * 
     * Code to add :
     * 
     * <pre>
     *   \@Before
     *  public void prepareTest(Scenario scenario) { 
     *      if (scenario != null) { 
     *      BrowserDriver.scenario = scenario;
     *      } 
     *   }
     * </pre>
     * 
     * @param pTitle (String)
     * @param pText (String)
     */
    public static void scenarioPrintToggleText(String pTitle, String pText) {

        if (scenario != null) {
            String unicId = UUID.randomUUID().toString();
            String html = "<a onclick=\"toggleDiv=document.getElementById('" + unicId
                    + "'); toggleDiv.style.display = (toggleDiv.style.display == 'none' ? 'block' : 'none');return false\" href=\"\">" + pTitle + "</a>"
                    + "<div id=\"" + unicId + "\" style=\"display:none ; white-space: pre-wrap; word-break:break-all'\">" + pText + "</div><br>";
            scenario.write(html);
        }
    }

    /**
     * Embed in scenario text. Mandatory add this code either to any descendant of a class with @ContextConfiguration annotation or to the class itself if it
     * doesn't have any descendants <br>
     * 
     * Code to add :
     * 
     * <pre>
     *   \@Before
     *  public void prepareTest(Scenario scenario) { 
     *      if (scenario != null) { 
     *      BrowserDriver.scenario = scenario;
     *      } 
     *   }
     * </pre>
     * 
     * @param pText (String)
     */
    public static void scenarioPrintText(String pText) {

        if (scenario != null) {
            scenario.write(pText + "<br>");

        }
    }

    /**
     * Returns the video URL if run on grid. Note: Works ONLY if gridExtra is deployed on the grid.
     * 
     * @return (String)
     */
    private static String getVideoUrl() {
        String result = null;

        if (gridFactory != null && gridFactory.getCurrentHub() != null) {
            try {
                URL url = new URL(gridFactory.getCurrentHub());
                result = url.getProtocol() + "://" + url.getHost() + ":3000/download_video/" + getSessionId() + ".mp4";
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                log.error("getVideoUrl", e);
            }
        }

        return result;
    }

    /**
     * Return the selenium-server session id
     * 
     * @return (String)
     */
    public static String getSessionId() {
        String result = null;
        if (mDriver != null) {
            result = ((RemoteWebDriver) mDriver).getSessionId().toString();
        }
        return result;
    }

    /**
     * Return the RemoteDriver capabilities
     * 
     * @return (String)
     */
    public static String getCapabilities() {
        String result = null;
        if (mDriver != null) {
            result = ((RemoteWebDriver) mDriver).getCapabilities().toString();
        }
        return result;
    }

    /**
     * Send an ENTER key to the field
     * 
     * @param pLocator A By locator that will be typed into
     */
    public static void sendEnterKey(By pLocator) {
        sendKey(pLocator, Keys.ENTER);
    }

    /**
     * Send a key to the field
     * 
     * @param pLocator A By locator that will be typed into
     * 
     * @param pKeys The key send to the field (ex: ENTER)
     */
    public static void sendKey(By pLocator, Keys pKeys) {
        // QUALENG-94 (mremillard): Calling the sendKeys() method.
        sendKeys(pLocator, pKeys);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and will send the "pKeys" to the WebElement
     * specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pKeys - A CharSequence.
     */
    public static void sendKeys(Object pObject, Keys pKeys) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Sending the keys to the WebElement.
            weal.webElement.sendKeys(pKeys);

            // Logging.
            String logMessage = "Sent key(s) '" + pKeys.toString() + "' to the WebElement defined as '" + weal.locator + "'.";
            log.info(logMessage);
        } catch (Exception e) {
            handleException(e, "type");
        }
    } // sendKeys()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void type(By pLocator, String pString) {
        type((Object) pLocator, pString);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and will send the "pString" to the WebElement
     * specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pString - The String to type.
     */
    public static void type(Object pObject, String pString) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Sending the String to the WebElement.
            weal.webElement.sendKeys(pString);

            // Logging.
            String logMessage = "Typed String '" + pString + "' in the WebElement defined as '" + weal.locator + "'.";
            log.info(logMessage);
        } catch (Exception e) {
            handleException(e, "type");
        }
    } // type()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void secureType(By pLocator, String pString) {
        secureType((Object) pLocator, pString);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and will send the "pString" to the WebElement
     * specified/found.
     * 
     * This method is "secure" in-so-far as it doesn't log what was typed and to what WebElement.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pString - The String to type.
     */
    public static void secureType(Object pObject, String pString) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Sending the String to the WebElement.
            weal.webElement.sendKeys(pString);

            // Logging.
            log.info("Typed secure info.");
        } catch (Exception e) {
            handleException(e, "secureType");
        }
    } // secureType()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void clear(By pLocator) {
        clear((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will "clear" the WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void clear(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Clearing the field
            weal.webElement.clear();

            // Logging.
            String logMessage = "Cleared the WebElement defined as '" + weal.locator + "'.";
            log.info(logMessage);
        } catch (Exception e) {
            handleException(e, "clear");
        }
    } // clear()

    /**
     * Submit a page and wait for the elementLocator to return on the new page.
     * 
     * @param pLocator Locator for submit button
     * @param pElementLocator Locator for element on new page
     */
    public static void submit(By pLocator, By pElementLocator) {
        try {
            log.info("Submitting " + pLocator);
            // Find the text input element by its name
            WebElement element = mDriver.findElement(pLocator);
            // Enter something to search for
            element.submit();
            // WebDriverWait wait = new WebDriverWait(mDriver, timeoutInSeconds);
            // log.info("Waiting for " + locator);
            // wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
        } catch (Exception e) {
            handleException(e, "submit");
        }

        waitForElementPresent(pElementLocator);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void click(By pLocator) {
        click((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will "click" the WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void click(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Clicking on the WebElement when it is clickable.
            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeoutInSeconds);
            wait.until(ExpectedConditions.elementToBeClickable(weal.webElement)).click();

            // Logging.
            log.info("Clicked the WebElement defined as '" + weal.locator + "'.");
        } catch (Exception e) {
            handleException(e, "click");
        }
    } // click()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void clickJS(By pLocator) {
        clickJS((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will "click" the WebElement specified/found using JavaScript.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void clickJS(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriver driver = getCurrentDriver();
            WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
            wait.until(ExpectedConditions.elementToBeClickable(weal.webElement));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", weal.webElement);

            // Logging.
            log.info("Clicked (using JS) the WebElement defined as '" + weal.locator + "'.");
        } catch (Exception e) {
            handleException(e, "clickJS");
        }
    } // clickJS()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void scrollAndClick(By pLocator) {
        scrollAndClick((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will scroll the WebElement specified/found into view and
     * "click" on it.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void scrollAndClick(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Scrolling the WebElement into view and clicking on it.
            scrollIntoView(weal.webElement);
            weal.webElement.click();

            // Logging.
            log.info("Clicked the WebElement defined as '" + weal.locator + "'.");
        } catch (Exception e) {
            handleException(e, "scrollAndClick");
        }
    } // scrollAndClick()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void scrollIntoView(By pLocator) {
        scrollIntoView((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will scroll the WebElement specified/found into view.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void scrollIntoView(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // Scrolling the WebElement into view.
            scrollIntoView(weal.webElement);

            // Logging.
            log.info("Scrolled the WebElement defined as '" + weal.locator + "' into view.");
        } catch (Exception e) {
            handleException(e, "scrollIntoView");
        }
    } // scrollIntoView()

    private static void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) mDriver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void switchToFrame(By pLocator, String pFrame) {
        switchToFrame((Object) pLocator, pFrame);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter, will switch to the specified iFrame and wait for the
     * WebElement specified/found to be present.
     * 
     * @param pObject - A WebElement or a By "locator" an element we wish to find.
     * @param pFrame - The name or ID of the iFrame we wish to switch to.
     */
    public static boolean switchToFrame(Object pObject, String pFrame) {
        boolean result = false;

        try {
            log.info("Switching to Frame: " + pFrame);
            mDriver.switchTo().frame(pFrame);

            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if ((weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            WebDriverWait wait = new WebDriverWait(mDriver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(getBy(weal.locator)));

            result = true;
        } catch (Exception e) {
            handleException(e, "switchToFrame");
        }

        return result;
    } // switchToFrame()

    /**
     * Close the browser
     */
    private static class BrowserCleanup implements Runnable {
        @Override
        public void run() {

            if (!firefoxDebugFlag) {
                log.info("Closing the browser");

                try {
                    close();
                } catch (Exception e) {
                    // log.error(ExceptionUtils.getRootCauseMessage(e));
                    // screenShot("BrowserCleanup");
                    handleException(e, "BrowserCleanup");
                }
            }
        }
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isElementPresent(By pLocator) {
        return isElementPresent((Object) pLocator);
    }

    // QUALENG-97 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns whether or not the WebElement specified/found is
     * present.
     * 
     * @param pObject - A WebElement or a By "locator".
     *
     * @return (boolean) - Whether or not the WebElement is present.
     */
    public static boolean isElementPresent(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if ((weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            log.info("Finding the WebElement defined as '" + weal.locator + "'.");

            getCurrentDriver().findElement(getBy(weal.locator));

            return true;
        } catch (Exception e) {
            screenShot("isElementPresent");
            return false;
        }
    } // isElementPresent()

    // QUALENG-103 (mremillard).
    /**
     * This method is like the waitForElementPresent() and the isElementPresent() methods in that it waits for the element to be present without ever throwing
     * an exception - it will keep checking for the presence of the specified WebElement, as long as the timeout allows, but in the end it will return how much
     * of the allowed timeout remains.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds we want the method to wait.
     *
     * @return (long) - The time left in the allowed timeout. If the element was never present then 0 will be returned.
     */
    private static long waitForElementToBePresent(Object pObject, long pTimeout) {
        long timeLeft = pTimeout;
        long startTime = (new Date()).getTime();

        try {
            waitForElementPresent(pObject, pTimeout);
        } catch (Exception e) {
            // Do nothing.
        }

        long endTime = (new Date()).getTime();
        long elapsedTime = endTime - startTime;
        long elapsedTimeInSeconds = ((elapsedTime > 500) && (elapsedTime < 1000)) ? 1 : elapsedTime / 1000;

        if (elapsedTimeInSeconds >= pTimeout) {
            timeLeft = 0;
        } else {
            timeLeft = pTimeout - elapsedTimeInSeconds;
        }

        return timeLeft;
    } // waitForElementToBePresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isElementNotPresent(By pLocator) {
        return isElementNotPresent((Object) pLocator);
    }

    // QUALENG-97 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns whether or not the WebElement specified/found is not
     * present.
     * 
     * @param pObject - A WebElement or a By "locator".
     *
     * @return (boolean) - Whether or not the WebElement is not present.
     */
    public static boolean isElementNotPresent(Object pObject) {
        boolean result = false;

        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if (inDebugMode && (weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            log.info("Checking whether or not the WebElement defined as '" + weal.locator + "' is not present.");

            // 2014/10/27 (DPA) Use the good way to test the non presence of element, according to WebDriver doc.
            // 2014/10/28 (DPA) inverse condition (impact of no test with this project)
            if (weal.webElement == null) {
                result = true;
            }
        } catch (NoSuchElementException nsee) {
            result = true;
        } catch (Exception e) {
            handleException(e, "isElementNotPresent()");
        }

        return result;
    } // isElementNotPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isElementVisible(By pLocator) {
        return isElementVisible((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns whether or not the WebElement specified/found
     * "is displayed".
     * 
     * @param pObject - A WebElement or a By "locator".
     *
     * @return (boolean) - Whether or not the WebElement is displayed.
     */
    public static boolean isElementVisible(Object pObject) {
        boolean result = false;

        String locator = null;
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);
            locator = weal.locator;

            // Checking whether or not the WebElement is visible/displayed.
            result = weal.webElement.isDisplayed();

            // Logging.
            if (result) {
                log.info("The WebElement defined as '" + weal.locator + "' is visible.");
            } else {
                log.info("The WebElement defined as '" + weal.locator + "' is not visible.");
            }
        } catch (Exception e) {
            log.info("The WebElement defined as '" + locator + "' does not appear to be visible.");
        }

        return result;
    } // isElementVisible()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isTextPresent(By pLocator, String pString) {
        return isTextPresent((Object) pLocator, pString);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and will search the WebElement specified/found for specific
     * text.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pString - The text whose presence we wish to know.
     * 
     * @return (boolean) - Whether or not the WebElement's text contains the specified text.
     */
    public static boolean isTextPresent(Object pObject, String pString) {
        boolean result = false;

        String locator = null;
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);
            locator = weal.locator;

            // Checking whether or not the WebElement contains the specified text.
            result = weal.webElement.getText().contains(pString);

            // Logging.
            if (result) {
                log.info("The WebElement defined as '" + weal.locator + "' contains the text '" + pString + "'.");
            } else {
                log.info("The WebElement defined as '" + weal.locator + "' does not contain the text '" + pString + "'.");
            }
        } catch (Exception e) {
            log.info("The WebElement defined as '" + locator + "' does not appear to contain the text '" + pString + "'.");
        }

        return result;
    } // isTextPresent()

    /**
     * Check if the url matches the src attribute.
     * 
     * @param pLocator By locator for element
     * @param pString URL for src
     * @return Succeeded or Failed
     */
    public static boolean isImageSrcPresent(By pLocator, String pString) {
        log.info("Is image src " + pString + " at locator: " + pLocator);
        return isAttributeValueEqual(pLocator, "src", pString);
    }

    /**
     * Validate attribute with locator, attribute name and string in attribute
     * 
     * @param pLocator By locator for element
     * @param pAttributeName HTML attribute name
     * @return Succeeded or Failed
     */
    public static boolean isAttributePresent(By pLocator, String pAttributeName) {
        return isAttributePresent((Object) pLocator, pAttributeName, true);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isAttributePresent(By pLocator, String pAttributeName, Boolean pLogging) {
        return isAttributePresent((Object) pLocator, pAttributeName, pLogging);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and returns whether or not the WebElement specified/found
     * contains the specified attribute.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttributeName - An HTML attribute name.
     * @param pLogging - Whether or not we wish to log the process.
     *
     * @return (boolean) - Whether or not the WebElement contains the specified attribute
     */
    public static boolean isAttributePresent(Object pObject, String pAttributeName, Boolean pLogging) {
        boolean result = false;

        String locator = null;
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);
            locator = weal.locator;

            // Checking whether or not the WebElement contains the attribute.
            String attribute = weal.webElement.getAttribute(pAttributeName);
            result = (attribute != null);

            // Logging.
            if (pLogging) {
                if (result) {
                    log.info("The WebElement defined as '" + weal.locator + "' contains the attribute '" + pAttributeName + "'.");
                } else {
                    log.info("The WebElement defined as '" + weal.locator + "' does not contain the attribute '" + pAttributeName + "'.");
                }
            }
        } catch (Exception e) {
            if (pLogging) {
                log.info("The WebElement defined as '" + locator + "' does not appear to contain the attribute '" + pAttributeName + "'.");
            }
        }

        return result;
    } // isAttributePresent()

    /**
     * Is text present in the URL of the page you are currently on?
     * 
     * @param string String to validate
     * @return Succeeded or Failed
     */
    public static boolean isUrlPresent(String string) {
        log.info("Is '" + string + "' present in the url.");

        try {
            return (getCurrentURL().contains(string));
        } catch (Exception e) {
            return false;
        }
    } // isUrlPresent()

    /**
     * Validate attribute with locator, attribute name and string in attribute
     * 
     * @param pLocator By locator for element
     * @param pAttributeName HTML attribute name
     * @param pString value of attribute
     * @return Succeeded or Failed
     */
    public static boolean isAttributeValueEqual(By pLocator, String pAttributeName, String pString) {
        return isAttributeValueEqual(pLocator, pAttributeName, pString, true);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean isAttributeValueEqual(By pLocator, String pAttributeName, String pString, Boolean pLogging) {
        return isAttributeValueEqual((Object) pLocator, pAttributeName, pString, pLogging);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and returns whether or not the WebElement specified/found
     * has an attribute with a specific value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttributeName - An HTML attribute name.
     * @param pString - The value we are checking that the attribute contains.
     * @param pLogging - Whether or not we wish to log the process.
     *
     * @return (boolean) - Whether or not the WebElement has an attribute which contains a specific value.
     */
    public static boolean isAttributeValueEqual(Object pObject, String pAttributeName, String pString, Boolean pLogging) {
        boolean result = false;

        String locator = null;
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);
            locator = weal.locator;

            // Checking whether or not the WebElement contains an attribute which contains a specific value.
            String attribute = weal.webElement.getAttribute(pAttributeName);
            if (attribute != null) {
                result = attribute.contains(pString);
            }

            // Logging.
            if (pLogging) {
                if (result) {
                    log.info("The WebElement defined as '" + weal.locator + "' contains attribute '" + pAttributeName + "' which contains '" + pString + "'.");
                } else {
                    if (attribute == null) {
                        log.info("The WebElement defined as '" + weal.locator + "' does not contain attribute '" + pAttributeName + "'.");
                    } else {
                        log.info("The WebElement defined as '" + weal.locator + "' contains attribute '" + pAttributeName + "' but it does not contain '"
                                + pString + "'.");
                    }
                }
            }
        } catch (Exception e) {
            if (pLogging) {
                log.info("The WebElement defined as '" + locator + "' does not appear to contain an attribute named '" + pAttributeName + "' which contains '"
                        + pString + "'.");
            }
        }

        return result;
    } // isAttributeValueEqual()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static String getAttributeValue(By pLocator, String pAttributeName) {
        return getAttributeValue((Object) pLocator, pAttributeName);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and returns the WebElement specified/found specified
     * attribute's value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttributeName - An HTML attribute name.
     *
     * @return (String) - The value of the WebElement's attribute or null if none was found.
     */
    public static String getAttributeValue(Object pObject, String pAttributeName) {
        String result = null;

        String locator = null;
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);
            locator = weal.locator;

            // Retrieving the WebElement's attribute's value.
            result = weal.webElement.getAttribute(pAttributeName);

            // Logging.
            log.info("The WebElement's attribute '" + pAttributeName + "' contains '" + result + "'.");
        } catch (Exception e) {
            log.info("Error: Retrieving the value of attribute '" + pAttributeName + ", from the WebElement defined as '" + locator + "', has failed.");
        }

        return result;
    } // getAttributeValue()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitClick(By pLocator, int pMilliSecs) {
        waitClick((Object) pLocator, pMilliSecs);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter, waits for the WebElement specified/found specified to be
     * clickable, then waits the specified number of milliseconds and finally clicks on the WebElement.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pMilliSecs - Milliseconds to wait once the WebElement is seen as clickable.
     */
    public static void waitClick(Object pObject, int pMilliSecs) {
        BrowserDriver.waitForElementClickable(pObject, timeoutInSeconds);
        BrowserDriver.wait(pMilliSecs);
        BrowserDriver.click(pObject);
    } // waitClick()

    /**
     * Closing the browser.
     */
    public static synchronized void close() {
        try {
            if (mDriver == null) {
                log.info("browser already closed");
            } else {
                log.info("closing " + mDriver + " ..");
                mDriver.quit();
                mDriver = null;
                log.info("browser closed");
            }
        } catch (Exception e) {
            log.error("cannot close browser: " + mDriver, e);
        }
    } // close()

    /**
     * Wait for element and fail assertion if it doesn't show up in time.
     * 
     * @param pLocator By locator for element
     */
    public static void waitForElementPresent(By pLocator) {
        waitForElementPresent(pLocator, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitForElementPresent(By pLocator, long pTimeout) {
        return waitForElementPresent((Object) pLocator, pTimeout);
    }

    // QUALENG-97 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to be present.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds we want the method to wait.
     *
     * @return (boolean) - Whether or not the WebElement is present.
     */
    protected static boolean waitForElementPresent(Object pObject, long pTimeout) {
        boolean result = false;

        try {
            // QUALENG-103 (mremillard): Revamped this method to be more efficient and robust.

            By byElement = null;
            String locator = null;

            if (pObject instanceof By) {
                locator = getByLocator((By) pObject);
                byElement = (By) pObject;
            } else if (pObject instanceof WebElement) {
                locator = getWebElementLocator((WebElement) pObject);
                byElement = getBy(locator);
            }

            log.info("Waiting a maximum of {} seconds for the WebElement defined as {} to be present.", pTimeout, locator);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.presenceOfElementLocated(byElement));

            result = true;
        } catch (Exception e) {
            handleException(e, "waitForElementPresent");
        }

        return result;
    } // waitForElementPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitForElementClickable(By pLocator, long pTimeout) {
        return waitForElementClickable((Object) pLocator, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and returns whether or not the WebElement specified/found
     * is clickable after having respected a specified timeout period.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds we want the method to wait.
     *
     * @return (boolean) - Whether or not the WebElement is clickable.
     */
    protected static boolean waitForElementClickable(Object pObject, long pTimeout) {
        boolean result = false;

        String locator = null;
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                // QUALENG-104 (mremillard): Removed the use of the WebElementAndLocator object because running the
                // "wait.until()" with a WebElement runs the risk of encountering a StaleElementReferenceException.

                // Checking whether or not the WebElement is clickable.
                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                if (pObject instanceof By) {
                    locator = getByLocator((By) pObject);
                    wait.until(ExpectedConditions.elementToBeClickable((By) pObject));
                } else if (pObject instanceof WebElement) {
                    if (inDebugMode) {
                        locator = getWebElementLocator((WebElement) pObject);
                    }
                    wait.until(ExpectedConditions.elementToBeClickable((WebElement) pObject));
                } else {
                    throw new InvalidParameterException("The \"pObject\" parameter must be either a By or a WebElement.");
                }

                result = true;

                // Logging.
                log.info("The WebElement defined as '" + locator + "' is clickable.");
            } else {
                throw new NoSuchElementException("The element is not clickable because it is not present.");
            }
        } catch (Exception e) {
            log.info("The WebElement defined as '" + locator + "' appears to not be clickable.");

            if (e instanceof TimeoutException) {
                // Do nothing.
            } else {
                handleException(e, "waitForElementClickable");
            }
        }

        return result;
    } // waitForElementClickable()

    public static void waitForElementClickable(By pLocator) {
        waitForElementClickable(pLocator, timeoutInSeconds);
    }

    /**
     * Wait for an element to become visible with standard wait time
     * 
     * @param pLocator By locator for element
     */
    public static void waitForElementVisible(By pLocator) {
        waitForElementVisible(pLocator, timeoutInSeconds);
    }

    /**
     * Wait for an element to become invisible with standard wait time
     * 
     * @param pLocator By locator for element
     */
    public static void waitForElementNotVisible(By pLocator) {
        waitForElementNotVisible(pLocator, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForElementVisible(By pLocator, long pTimeout) {
        waitForElementVisible((Object) pLocator, pTimeout);
    }

    // QUALENG-97 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and waits for the the WebElement specified/found to be visible.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds we want the method to wait.
     */
    protected static void waitForElementVisible(Object pObject, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Revamped this method to be more efficient and robust.

            By byElement = null;
            String locator = null;

            if (pObject instanceof By) {
                locator = getByLocator((By) pObject);
                byElement = (By) pObject;
            } else if (pObject instanceof WebElement) {
                locator = getWebElementLocator((WebElement) pObject);
                byElement = getBy(locator);
            }

            log.info("Waiting a maximum of {} seconds for the WebElement defined as {} to be visible.", pTimeout, locator);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
        } catch (Exception e) {
            handleException(e, "waitForElementVisible");
        }
    } // waitForElementVisible()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForElementNotVisible(By pLocator, long pTimeout) {
        waitForElementNotVisible((Object) pLocator, pTimeout);
    }

    // QUALENG-97 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and waits for the the WebElement specified/found to be not
     * visible.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds we want the method to wait.
     */
    protected static void waitForElementNotVisible(Object pObject, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Revamped this method to be more efficient and robust.

            By byElement = null;
            String locator = null;

            if (pObject instanceof By) {
                locator = getByLocator((By) pObject);
                byElement = (By) pObject;
            } else if (pObject instanceof WebElement) {
                locator = getWebElementLocator((WebElement) pObject);
                byElement = getBy(locator);
            }

            log.info("Waiting a maximum of {} seconds for the WebElement defined as {} to be not visible.", pTimeout, locator);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(byElement));
        } catch (Exception e) {
            handleException(e, "waitForElementNotVisible");
        }
    } // waitForElementNotVisible()

    /**
     * Wait for all elements found with pLocator are not visible with default timeout
     * 
     * @param pLocator (By)
     * @return (boolean) if not visible
     */
    public static boolean waitForElementsNotVisible(By pLocator) {
        return waitForElementsNotVisible(pLocator, timeoutInSeconds);
    }

    /**
     * Waiting for all elements specified to be not visible.
     * 
     * @param pLocator (By) - A list of web element locators.
     * @param pTimeout (long) - The maximum number of seconds to wait.
     * 
     * @return (boolean) - Whether or not the elements are all not visible.
     */
    public static boolean waitForElementsNotVisible(By pLocator, long pTimeout) {
        boolean isVisible = false;

        // QUALENG-90 (mremillard): Revamped this method to check the time properly and to not call
        // isElementVisible() once out of the while() loop.

        long startTime = (new Date()).getTime();
        List<WebElement> lElements = BrowserDriver.findElements(pLocator);

        for (WebElement element : lElements) {
            isVisible = isElementVisible(element);
            while (isVisible) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long currentTime = (new Date()).getTime();
                if (((currentTime - startTime) / 1000) > pTimeout) {
                    // We've run out of time so are breaking out of the while() loop.
                    break;
                } else {
                    isVisible = isElementVisible(element);
                }
            }

            if (isVisible) {
                // Breaking out of the for() loop because one of the elements in the list is
                // visible.
                break;
            }
        }

        return !isVisible;
    } // waitForElementsNotVisible()

    /**
     * Wait for text to become present with standard wait time
     * 
     * @param pLocator By locator for element
     * @param pString String to validate
     */
    public static void waitForTextPresent(By pLocator, String pString) {
        waitForTextPresent((Object) pLocator, pString, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForTextPresent(By pLocator, String pString, long pTimeout) {
        waitForTextPresent((Object) pLocator, pString, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to contain
     * some specific text.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pString - The text we will wait for.
     * @param pTimeout - The maximum number of seconds to wait.
     */
    protected static void waitForTextPresent(Object pObject, String pString, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                WebElementAndLocator weal = new BrowserDriver.WebElementAndLocator();
                try {
                    weal = getWebElementAndLocator(pObject);
                } catch (NoSuchElementException nsee) {
                    // Do nothing.
                }

                // Logging.
                log.info("Waiting for text '" + pString + "' to be present in the WebElement defined as '" + weal.locator + "' for a maximum of " + pTimeout
                        + " seconds.");

                // Waiting for the text to be present in the WebElement.
                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                wait.until(ExpectedConditions.textToBePresentInElement(weal.webElement, pString));
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            handleException(e, "waitForTextPresent");
        }
    } // waitForTextPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForAttributePresent(By pLocator, String pAttribute, long pTimeout) {
        waitForAttributePresent((Object) pLocator, pAttribute, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute to be present in the
     * WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pTimeout - The maximum number of seconds to wait.
     */
    protected static void waitForAttributePresent(Object pObject, String pAttribute, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                WebElementAndLocator weal = new BrowserDriver.WebElementAndLocator();
                try {
                    weal = getWebElementAndLocator(pObject);
                } catch (NoSuchElementException nsee) {
                    // Do nothing.
                }

                // Logging.
                log.info("Waiting a maximum of {} seconds for attribute {} to be present in the WebElement defined as {}.", pTimeout, pAttribute, weal.locator);

                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                wait.until(new ExpectedCondition<Boolean>() {
                    private WebElement webElement;
                    private String attribute;

                    private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute) {
                        this.webElement = pWebElement;
                        this.attribute = pAttribute;

                        return this;
                    }

                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return isAttributePresent(webElement, attribute, false);
                    }
                }.init(weal.webElement, pAttribute));
            } else {
                throw new NoSuchElementException("Attribute '" + pAttribute + "' is not present because its element is " + "not present.");
            }
        } catch (Exception e) {
            handleException(e, "waitForAttributePresent");
        }
    } // waitForAttributePresent()

    /**
     * Wait for an attribute to be present
     * 
     * @param pLocator By locator for element
     * @param pAttribute HTML/CSS attribute
     */
    public static void waitForAttributePresent(By pLocator, String pAttribute) {
        waitForAttributePresent(pLocator, pAttribute, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForAttributeNotPresent(By pLocator, String pAttribute, long pTimeout) {
        waitForAttributeNotPresent((Object) pLocator, pAttribute, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute to not be present in the
     * WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pTimeout - The maximum number of seconds to wait.
     */
    protected static void waitForAttributeNotPresent(Object pObject, String pAttribute, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                WebElementAndLocator weal = getWebElementAndLocator(pObject);

                // Logging.
                log.info("Waiting a maximum of {} seconds for attribute {} to not be present in the WebElement defined as {}.", pTimeout, pAttribute,
                        weal.locator);

                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                wait.until(new ExpectedCondition<Boolean>() {
                    private WebElement webElement;
                    private String attribute;

                    private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute) {
                        this.webElement = pWebElement;
                        this.attribute = pAttribute;

                        return this;
                    }

                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return !isAttributePresent(webElement, attribute, false);
                    }
                }.init(weal.webElement, pAttribute));
            } else {
                throw new NoSuchElementException("Attribute '" + pAttribute + "' is not present because its element is " + "not present.");
            }
        } catch (Exception e) {
            handleException(e, "waitForAttributeNotPresent");
        }
    } // waitForAttributeNotPresent()

    /**
     * Wait for an attribute not to be present in seconds
     * 
     * @param pLocator By locator for element
     * @param pAttribute HTML/CSS attribute
     */
    public static void waitForAttributeNotPresent(By pLocator, String pAttribute) {
        waitForAttributeNotPresent(pLocator, pAttribute, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForAttributeValuePresent(By pLocator, String pAttribute, String pValue, long pTimeout) {
        waitForAttributeValuePresent((Object) pLocator, pAttribute, pValue, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute, from the WebElement
     * specified/found, to contain a certain value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pValue - The attribute value we will wait for.
     * @param pTimeout - The maximum number of seconds to wait.
     */
    protected static void waitForAttributeValuePresent(Object pObject, String pAttribute, String pValue, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                WebElementAndLocator weal = getWebElementAndLocator(pObject);

                // Logging.
                log.info("Waiting a maximum of {} seconds for an attribute {} to be present with a value of {} in the " + "WebElement defined as {}.", pTimeout,
                        pAttribute, pValue, weal.locator);

                // Waiting for the WebElement's attribute to contain a specific value.
                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                wait.until(new ExpectedCondition<Boolean>() {
                    private WebElement webElement;
                    private String attribute;
                    private String value;

                    private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute, String pValue) {
                        this.webElement = pWebElement;
                        this.attribute = pAttribute;
                        this.value = pValue;

                        return this;
                    }

                    @Override
                    public Boolean apply(WebDriver pWebDriver) {
                        return isAttributeValueEqual(webElement, attribute, value, false);
                    }
                }.init(weal.webElement, pAttribute, pValue));
            } else {
                throw new NoSuchElementException("Attribute '" + pAttribute + "' is not present because its element is " + "not present.");
            }
        } catch (Exception e) {
            handleException(e, " waitForAttributeValuePresent");
        }
    } // waitForAttributeValuePresent()

    /**
     * Wait for an attribute to have a value in seconds
     * 
     * @param pLocator By locator for element
     * @param pAttribute HTML/CSS attribute
     * @param pValue String value to compare
     */
    public static void waitForAttributeValuePresent(By pLocator, String pAttribute, String pValue) {
        waitForAttributeValuePresent(pLocator, pAttribute, pValue, timeoutInSeconds);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void waitForAttributeValueNotPresent(By pLocator, String pAttribute, String pValue, long pTimeout) {
        waitForAttributeValueNotPresent((Object) pLocator, pAttribute, pValue, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute, from the WebElement
     * specified/found, to not contain a certain value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pValue - The attribute value we will wait for.
     * @param pTimeout - The maximum number of seconds to wait.
     */
    protected static void waitForAttributeValueNotPresent(Object pObject, String pAttribute, String pValue, long pTimeout) {
        try {
            // QUALENG-103 (mremillard): Making sure first that if the WebElement is present.
            long timeLeft = waitForElementToBePresent(pObject, pTimeout);
            if (timeLeft > 0) {
                WebElementAndLocator weal = getWebElementAndLocator(pObject);

                // Logging.
                log.info("Waiting a maximum of {} seconds for attribute {} to not contain a value of {} in the WebElement " + "defined as {}.", pTimeout,
                        pAttribute, pValue, weal.locator);

                WebDriverWait wait = new WebDriverWait(getCurrentDriver(), timeLeft);
                wait.until(new ExpectedCondition<Boolean>() {
                    private WebElement webElement;
                    private String attribute;
                    private String value;

                    private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute, String pValue) {
                        this.webElement = pWebElement;
                        this.attribute = pAttribute;
                        this.value = pValue;

                        return this;
                    }

                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return !isAttributeValueEqual(webElement, attribute, value, false);
                    }
                }.init(weal.webElement, pAttribute, pValue));
            } else {
                throw new NoSuchElementException("Attribute '" + pAttribute + "' is not present because its element is " + "not present.");
            }
        } catch (Exception e) {
            handleException(e, "waitForAttributeValueNotPresent");
        }
    } // waitForAttributeValueNotPresent()

    /**
     * Wait for an attribute not to have a value in seconds
     * 
     * @param pLocator By locator for element
     * @param pAttribute HTML/CSS attribute
     * @param pValue String value to compare
     */
    public static void waitForAttributeValueNotPresent(By pLocator, String pAttribute, String pValue) {
        waitForAttributeValueNotPresent(pLocator, pAttribute, pValue, timeoutInSeconds);
    }

    /**
     * Get a list of elements by a locator
     * 
     * @param pLocator By locator for element
     * @return List of elements found
     */
    public static List<WebElement> findElements(By pLocator) {
        log.info("Find elements " + pLocator);
        List<WebElement> elements = null;
        try {
            elements = mDriver.findElements(pLocator);
        } catch (Exception e) {
            handleException(e, "findElements");
        }

        return elements;
    }

    /**
     * Sleep for a specific number of milliseconds
     * 
     * @param millisecs Milliseconds to wait
     */
    public static void wait(int millisecs) {
        log.info("Wait for " + millisecs + " milliseconds");

        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * Generate Random Name that starts with test
     * 
     * @return Test followed by time and number between 1-100
     */
    public static String randomName() {
        return (randomName("Test"));
    }

    /**
     * Generate Random Name that starts what ever value you pass
     * 
     * @param name first part of the return
     * @return param followed by time and number between 1-100
     */
    public static String randomName(String name) {
        log.info("Return random name starting with " + name);
        int ranNum = (int) (Math.random() * 10001);
        long time = System.currentTimeMillis();
        return (name + "_" + "_" + time + "_" + ranNum);
    }

    /**
     * Select from drop-down
     * 
     * @param locator By locator for element
     * @param value String value to compare
     * 
     * @deprecated
     */
    @Deprecated
    public static void selectDropdown(By pLocator, String pText) {
        selectDropdownByVisibleText(pLocator, pText);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void selectDropdownByIndex(By pLocator, int pIndex) {
        selectDropdownByIndex((Object) pLocator, pIndex);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and selects the option contained in the specified
     * <select> element (the WebElement specified/found) at the specified index.
     * 
     * @param pObject - A WebElement or a By "locator" representing a <select> element.
     * @param pIndex - The index of the option we want selected.
     */
    public static void selectDropdownByIndex(Object pObject, int pIndex) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            Select select = new Select(weal.webElement);
            select.selectByIndex(pIndex);
        } catch (Exception e) {
            handleException(e, "selectDropdownByIndex");
        }
    } // SelectDropdownByIndex()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void selectDropdownByValue(By pLocator, String pValue) {
        selectDropdownByValue((Object) pLocator, pValue);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and selects the option contained in the specified
     * <select> element (the WebElement specified/found) based on its value.
     * 
     * @param pObject - A WebElement or a By "locator" representing a <select> element.
     * @param pValue - The value we will use to select an option.
     */
    public static void selectDropdownByValue(Object pObject, String pValue) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            Select select = new Select(weal.webElement);
            select.selectByValue(pValue);
        } catch (Exception e) {
            handleException(e, "selectDropdownByValue");
        }
    } // selectDropdownByValue()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void selectDropdownByVisibleText(By pLocator, String pText) {
        selectDropdownByVisibleText((Object) pLocator, pText);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and selects the option contained in the specified
     * <select> element (the WebElement specified/found) whose visible text is like that which is specified.
     * 
     * @param pObject - A WebElement or a By "locator" representing a <select> element.
     * @param pText - The text we want the selected option to have.
     */
    public static void selectDropdownByVisibleText(Object pObject, String pText) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            Select select = new Select(weal.webElement);
            select.selectByVisibleText(pText);
        } catch (Exception e) {
            handleException(e, "selectDropdownByVisibleText");
        }
    } // selectDropdownByVisibleText()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean doesTableHaveRows(By pLocator) {
        return doesTableHaveRows((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns whether or not the WebElement specified/found (a
     * <table>
     * ) contains rows.
     * 
     * @param pObject - A WebElement or a By "locator" representing a
     *            <table>
     *            element.
     *
     * @return (boolean) - Whether or not the
     *         <table>
     *         WebElement contains rows (
     *         <tr>
     *         's).
     */
    public static boolean doesTableHaveRows(Object pObject) {
        boolean result = false;

        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            log.info("Does table '" + weal.locator + "' have rows?");
            List<WebElement> allRows = weal.webElement.findElements(By.tagName("tr"));
            if (allRows.isEmpty()) {
                log.info("Table '" + weal.locator + "' does not have any rows.");
            } else {
                log.info("Table '" + weal.locator + "' contains " + allRows.size() + " rows.");
                result = true;
            }
        } catch (Exception e) {
            // Do nothing.
        }

        return result;
    } // doesTableHaveRows()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static int rowsInTable(By pLocator) {
        return rowsInTable((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns the number of rows contained in the WebElement
     * specified/found (a
     * <table>
     * ).
     * 
     * @param pObject - A WebElement or a By "locator" representing a
     *            <table>
     *            element.
     *
     * @return (int) - The number of rows contained in the
     *         <table>
     *         WebElement.
     */
    public static int rowsInTable(Object pObject) {
        int rows = 0;

        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            log.info("How many rows does table '" + weal.locator + "' contain?");
            List<WebElement> allRows = weal.webElement.findElements(By.tagName("tr"));
            rows = allRows.size();
            log.info("There are " + rows + " rows in table '" + weal.locator + "'.");
        } catch (Exception e) {
            handleException(e, "rowsInTable");
        }

        return rows;
    } // rowsInTable()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static String getText(By pLocator) {
        return getText((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and returns the text associated with the WebElement
     * specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     *
     * @return (String) - The WebElement's text.
     */
    public static String getText(Object pObject) {
        String text = null;

        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            log.info("Retrieving the text for the WebElement defined as '" + weal.locator + "'.");
            text = weal.webElement.getText();
            log.info("The text found for the WebElement defined as '" + weal.locator + "' is '" + text + "'.");
        } catch (Exception e) {
            handleException(e, "getText");
        }

        return text;
    } // getText()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void moveToElement(By pLocator) {
        moveToElement((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and will move the page and mouse focus to the center of the
     * WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void moveToElement(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if ((weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            log.info("Moving to the WebElement defined as '" + weal.locator + "'.");

            WebDriver driver = getCurrentDriver();
            WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(BrowserDriver.getBy(weal.locator)));
            Actions action = new Actions(driver);
            action.moveToElement(weal.webElement).build().perform();
        } catch (Exception e) {
            handleException(e, "moveToElement");
        }
    } // moveToElement()

    /**
     * Move page focus by X Offset and by Y Offset
     * 
     * @param xoffset Offset from the top-left corner
     * @param yoffset Offset from the top-left corner
     */
    public static void moveByOffset(int xoffset, int yoffset) {
        log.info("Moving focus by X Offset " + xoffset + " and Y Offset " + yoffset);
        try {
            Actions action = new Actions(mDriver);
            action.moveByOffset(xoffset, yoffset);
            action.perform();
        } catch (Exception e) {
            handleException(e, "moveByOffset");
        }
    }

    /**
     * Return the right By object with classic Selenium locators.
     * 
     * @param pLocator (String) e.g. id=myid, link=click here, partiallink=cli, name=test, css=, classname= xpath=//table[@id=theRightTable],
     *            //table[@id=theRightTable]
     * 
     * @return (By)
     */
    public static By getBy(String pLocator) {
        By result = null;

        // QUALENG-98 (mremillard): This method was revamped in order to make it case-insensitive.

        int posEqualSign = pLocator.indexOf("=");
        if (posEqualSign > -1) {
            // Retrieving the locator's key.
            String key = pLocator.substring(0, posEqualSign).toLowerCase();

            if (key.equals("id")) {
                result = By.id(pLocator.substring(posEqualSign + 1));
            } else if (key.equals("name")) {
                result = By.name(pLocator.substring(posEqualSign + 1));
            } else if (key.equals("link")) {
                if (!pLocator.endsWith("*")) {
                    result = By.linkText(pLocator.substring(posEqualSign + 1));
                } else {
                    // To support legacy selenium if link=test* will change for partiallink=test
                    String newLocator = pLocator.substring(posEqualSign + 1, pLocator.length() - 1);
                    result = By.partialLinkText(newLocator);
                }
            } else if (key.equals("partiallink")) {
                result = By.partialLinkText(pLocator.substring(posEqualSign + 1));
            } else if (key.equals("xpath")) {
                result = By.xpath(pLocator.substring(posEqualSign + 1));
            } else if (key.equals("css") || key.equals("selector") || key.equals("cssselector")) {
                result = By.cssSelector(pLocator.substring(posEqualSign + 1));
            } else if (key.equals("classname")) {
                result = By.className(pLocator.substring(posEqualSign + 1));
            }
        } else if (pLocator.startsWith("//")) {
            result = By.xpath(pLocator);
        } else {
            log.error("Unsupported locator type (" + pLocator + ")!");
        }

        return result;
    } // getBy()

    /**
     * Return the title of the current page in the browser
     * 
     * @return (String)
     */
    public static String getTitle() {
        String result = null;

        try {
            result = mDriver.getTitle();
        } catch (Exception e) {
            handleException(e, "getTitle");
        }

        return result;
    }

    /**
     * Take a snapshot, log the exception and throw RuntimeException If screenshotName is blank, no snapshot will be take.
     * 
     * @param ex (Exception)
     * @param screenshotName (String)
     */
    private static void handleException(Exception ex, String screenshotName) {
        if (!StringUtils.isBlank(screenshotName)) {
            screenShot(screenshotName);
        }
        // QUALENG-42
        if (isIncludeHtmlSourceOnError()) {
            getPageSource();
        }
        String errorText = ExceptionUtils.getRootCauseMessage(ex);
        log.error("{} : {} ", screenshotName, errorText);
        throw new RuntimeException(ex);

    }

    // Needed for Spring

    /**
     * Running local or on a selenium grid
     * 
     * @param whereToRun Should the test be run in a local browser or on selenium grid
     */
    public static void setWhereToRun(String whereToRun) {
        BrowserDriver.whereToRun = whereToRun;
    }

    /**
     * Browser type Firefox/IE/Chrome
     * 
     * @param browserName Browser name Firefox/IE/Chrome
     */
    public static void setBrowserName(String browserName) {
        BrowserDriver.browserName = browserName;
    }

    /**
     * Set path to chrome driver.
     * 
     * @param pathToChrome The path to chrome on your local machine
     */
    public static void setPathToChrome(String pathToChrome) {
        BrowserDriver.pathToChrome = pathToChrome;
    }

    public static void setPathToPhantomjs(String pathToPhantomjs) {
        BrowserDriver.pathToPhantomjs = pathToPhantomjs;
    }

    public static void setFirefoxDebugFlag(Boolean firefoxDebugFlag) {
        BrowserDriver.firefoxDebugFlag = firefoxDebugFlag;
    }

    public static void setPathToFirebug(String pathToFirebug) {
        BrowserDriver.pathToFirebug = pathToFirebug;
    }

    public static void setPathToFirepath(String pathToFirepath) {
        BrowserDriver.pathToFirepath = pathToFirepath;
    }

    public static void setScenario(Scenario scenario) {
        BrowserDriver.scenario = scenario;
    }

    /**
     * Set the timeout for all wait action. Default is 60 seconds
     * 
     * @param timeoutInSeconds
     */
    public static void setTimeoutInSeconds(long timeoutInSeconds) {
        // 2014/10/27 (DPA)
        BrowserDriver.timeoutInSeconds = timeoutInSeconds;
    }

    /**
     * Get the timeout for all wait action. Default is 60 seconds
     * 
     * @param timeoutInSeconds
     */
    public static long getTimeoutInSeconds() {
        return BrowserDriver.timeoutInSeconds;
    }

    /**
     * Overide the selenium grid hub URL keep for backward compatibility (set the value in listOfGridHub)
     * 
     * @param gridUrl (String)
     * 
     */
    public static void setGridUrl(String gridUrl) {
        // 2014/11/12 (DPA) keep for backward compatibility (set the value in listOfGridHub)
        BrowserDriver.listOfGridHub = Arrays.asList(gridUrl);
    }

    /**
     * Use by spring to customize the list of hub, use by the GridFactory
     * 
     * @param listOfGridHub (List<String>)
     */
    public static void setListOfGridHub(List<String> listOfGridHub) {
        BrowserDriver.listOfGridHub = listOfGridHub;
    }

    /**
     * Set the ListOfGridHub with a String with the separetor ,
     * 
     * @param pListOfGridHub (String)
     */
    public static void setListOfGridHub(String pListOfGridHub) {
        String[] arrayHub = pListOfGridHub.split(",");
        BrowserDriver.listOfGridHub = Arrays.asList(arrayHub);
    }

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void mouseOver(By pLocator) {
        mouseOver((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and it will simulate moving the mouse over the WebElement
     * specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void mouseOver(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            Actions builder = new Actions(getCurrentDriver());
            builder.moveToElement(weal.webElement).build().perform();
        } catch (Exception e) {
            handleException(e, "mouseOver");
        }
    } // mouseOver()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void mouseOverJS(By pLocator) {
        mouseOverJS((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and it will simulate moving the mouse over the WebElement
     * specified/found via Javascript.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void mouseOverJS(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            javaScriptExecute(weal.webElement, "arguments[0].onmouseover();");
            log.info("mouseOverJS");
        } catch (Exception e) {
            handleException(e, "mouseOverJS");
        }
    } // mouseOverJS()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static void mouseOutJS(By pLocator) {
        mouseOutJS((Object) pLocator);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its parameter and it will simulate moving the mouse out from over the
     * WebElement specified/found via Javascript.
     * 
     * @param pObject - A WebElement or a By "locator".
     */
    public static void mouseOutJS(Object pObject) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            javaScriptExecute(weal.webElement, "arguments[0].onmouseout();");
            log.info("mouseOutJS");
        } catch (Exception e) {
            handleException(e, "mouseOutJS");
        }
    } // mouseOutJS()

    // QUALENG-98 (mremillard): Changed this method so that now it returns something.
    /**
     * This method executes some Javascript using the specified WebElement.
     * 
     * @param pWebElement - The WebElement we want to execute some Javascript code with.
     * @param pJavaScriptCode - The Javascript code we want executed. i.e.: "arguments[0].click();"
     * 
     * @return (Object) - The return from the Javascript code.
     */
    public static Object javaScriptExecute(WebElement pWebElement, String pJavaScriptCode) {
        Object result = null;

        try {
            JavascriptExecutor js = (JavascriptExecutor) mDriver;
            result = js.executeScript(pJavaScriptCode, pWebElement);
        } catch (Exception e) {
            handleException(e, "javaScriptExecute");
        }

        return result;
    } // javaScriptExecute()

    // QUALENG-94 (mremillard).
    // QUALENG-98 (mremillard): Changed this method so that it returns an Object rather than a String.
    /**
     * This method will have the WebDriver execute the specified Javascript.
     * 
     * @param pJavascriptCode - The Javascript code we want executed.
     * 
     * @return (Object) - The result returned by the Javascript code.
     */
    public static Object executeJavascript(String pJavascriptCode) {
        Object result = null;

        try {
            JavascriptExecutor jsExec = (JavascriptExecutor) getCurrentDriver();
            result = jsExec.executeScript(pJavascriptCode);
        } catch (Throwable t) {
            // Do nothing.
        }

        return result;
    } // executeJavascript()

    /**
     * Return a list of window handles which can be used to iterate over all open windows of this WebDriver instance by passing them to
     * switchTo().Options.window()
     * 
     * @return (List<String>)
     */
    public static List<String> getWindowHandles() {
        List<String> windowHandles = new ArrayList<String>();

        try {
            windowHandles = new ArrayList<String>(mDriver.getWindowHandles());
        } catch (Exception e) {
            handleException(e, "getWindowHandles");
        }

        return windowHandles;
    }

    public static boolean isIncludeVideoLink() {
        return includeVideoLink;
    }

    public static void setIncludeVideoLink(boolean includeVideoLink) {
        BrowserDriver.includeVideoLink = includeVideoLink;
    }

    /**
     * This method receives a DropDown and a list of expected selected options and will validate whether or not the DropDown has the exact list of selected
     * options.
     * 
     * Note: The list of options contains the options' visible inner text (i.e.: getText()).
     * 
     * @param pDropDown - A locator representing a DropDown (<select> element).
     * @param pExpectedSelectedOptions - A list of Strings representing DropDown options.
     * 
     */
    public static boolean validateDropDownSelectedOptions(String pDropDown, List<String> pExpectedSelectedOptions) {
        boolean result = false;

        WebElement dropDown = mDriver.findElement(BrowserDriver.getBy(pDropDown));
        if ((dropDown != null) && dropDown.getAttribute("type").contains("select")) {
            int expectedSize = pExpectedSelectedOptions.size();

            // Retrieving the actual list of selected options.
            Select select = new Select(dropDown);
            List<WebElement> selectedOptions = select.getAllSelectedOptions();

            // Making sure that we have the right number of selected options.
            if (selectedOptions.size() == expectedSize) {
                // Checking each selected option in turn.
                boolean expectedOptionsSelected = true;
                for (WebElement anOption : selectedOptions) {
                    // Retrieving the text of the current option.
                    String anOptionText = anOption.getText();

                    // Checking that each option selected is expected to be
                    // selected.
                    boolean optionExpected = false;
                    for (String anExpectedOption : pExpectedSelectedOptions) {
                        anExpectedOption = anExpectedOption.trim();
                        if (anOptionText.equals(anExpectedOption)) {
                            // The current selected option was expected.
                            optionExpected = true;
                            break;
                        }
                    }

                    if (!optionExpected) {
                        // At least one of the selected options was not expected.
                        expectedOptionsSelected = false;
                        break;
                    }
                }

                if (expectedOptionsSelected) {
                    log.info("All of the expected options were selected.");
                    result = true;
                } else {
                    log.error("Not all of the expected options were selected.");
                }
            } else {
                log.error("The wrong number of options were selected.");
            }
        } else {
            log.error("Unexpectedly the WebElement specified was not a DropDown.");
        }

        return result;
    } // validateDropDownSelectedOptions()

    public static boolean isIncludeHtmlSourceOnError() {
        return includeHtmlSourceOnError;
    }

    public static void setIncludeHtmlSourceOnError(boolean includeHtmlSourceOnError) {
        BrowserDriver.includeHtmlSourceOnError = includeHtmlSourceOnError;
    }

    /**
     * Set size of the current browser
     * 
     * @param pDimension
     */
    public static void setBrowserSize(Dimension pDimension) {
        try {
            mDriver.manage().window().setSize(new org.openqa.selenium.Dimension(pDimension.width, pDimension.height));
            String text = "Set browser size to: " + pDimension.width + "x" + pDimension.height;
            log.info(text);
            scenarioPrintText(text);
        } catch (Exception e) {
            handleException(e, "setBrowserSize");
        }
    }

    private static void applyInnerDimensionToBrowser() {
        Dimension dim = getInnerDimension();
        setBrowserSize(dim);
    }

    /**
     * Return the dimension of the browser If the browser was maximize, the value return is the client screen size.<br>
     * The browser dimension include the menu bar of the browser. <br>
     * To get inner Dimension (view-port) see: BrowserDriver.getInnerDimension().
     * 
     * @return (java.awt.Dimension)
     */
    public static Dimension getBrowserDimension() {
        Dimension result = null;

        try {
            org.openqa.selenium.Dimension windowSize = mDriver.manage().window().getSize();
            result = new Dimension(windowSize.getWidth(), windowSize.getHeight());
        } catch (Exception e) {
            handleException(e, "getScreenSize");
        }

        return result;
    } // getBrowserDimension()

    /**
     * Use JavaScript to return the inner size of the Browser (View-port)(without browser menu bar) <br>
     * To get Browser size with menu bar see: BrowserDRiver.getBrowserDimension()
     * 
     * @return (java.awt.Dimension)
     */
    public static Dimension getInnerDimension() {
        Dimension result = null;
        JavascriptExecutor js;
        try {
            if (mDriver instanceof JavascriptExecutor) {
                js = (JavascriptExecutor) mDriver;

                int width = Integer.valueOf(js.executeScript("return Math.max(document.documentElement.clientWidth, window.innerWidth || 0)").toString());
                int height = Integer.valueOf(js.executeScript("return Math.max(document.documentElement.clientHeight, window.innerHeight || 0)").toString());

                result = new Dimension(width, height);
            }
        } catch (Exception e) {
            handleException(e, "getInnerDimension");
        }

        return result;
    } // getInnerDimension()

    // ****************************************************************************************************
    // The "waitIs..." methods follow.
    // ****************************************************************************************************
    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsAttributeNotPresent(By pLocator, String pAttribute, long pTimeout) {
        return waitIsAttributeNotPresent((Object) pLocator, pAttribute, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute to not be present in the
     * WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement's attribute is not present.
     */
    protected static boolean waitIsAttributeNotPresent(Object pObject, String pAttribute, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private WebElement webElement;
                private String attribute;

                private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute) {
                    this.webElement = pWebElement;
                    this.attribute = pAttribute;

                    return this;
                }

                @Override
                public Boolean apply(WebDriver webDriver) {
                    return !isAttributePresent(webElement, attribute, false);
                }
            }.init(weal.webElement, pAttribute));

            return true;
        } catch (NoSuchElementException nsee) {
            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsAttributeNotPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsAttributePresent(By pLocator, String pAttribute, long pTimeout) {
        return waitIsAttributePresent((Object) pLocator, pAttribute, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute to be present in the
     * WebElement specified/found.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement's attribute is present.
     */
    protected static boolean waitIsAttributePresent(Object pObject, String pAttribute, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private WebElement webElement;
                private String attribute;

                private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute) {
                    this.webElement = pWebElement;
                    this.attribute = pAttribute;

                    return this;
                }

                @Override
                public Boolean apply(WebDriver webDriver) {
                    return isAttributePresent(webElement, attribute, false);
                }
            }.init(weal.webElement, pAttribute));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsAttributePresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsAttributeValueNotPresent(By pLocator, String pAttribute, String pValue, long pTimeout) {
        return waitIsAttributeValueNotPresent((Object) pLocator, pAttribute, pValue, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute in the WebElement
     * specified/found to not contain a specific value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement's attribute does not contain a particular value.
     */
    protected static boolean waitIsAttributeValueNotPresent(Object pObject, String pAttribute, String pValue, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private WebElement webElement;
                private String attribute;
                private String value;

                private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute, String pValue) {
                    this.webElement = pWebElement;
                    this.attribute = pAttribute;
                    this.value = pValue;

                    return this;
                }

                @Override
                public Boolean apply(WebDriver webDriver) {
                    return !isAttributeValueEqual(webElement, attribute, value, false);
                }
            }.init(weal.webElement, pAttribute, pValue));

            return true;
        } catch (NoSuchElementException nsee) {
            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsAttributeValueNotPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsAttributeValuePresent(By pLocator, String pAttribute, String pValue, long pTimeout) {
        return waitIsAttributeValuePresent((Object) pLocator, pAttribute, pValue, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for a specific attribute in the WebElement
     * specified/found to have a specific value.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pAttribute - An HTML attribute name.
     * @param pValue - The value we want the attribute to have.
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement's attribute contains a particular value.
     */
    protected static boolean waitIsAttributeValuePresent(Object pObject, String pAttribute, String pValue, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private WebElement webElement;
                private String attribute;
                private String value;

                private ExpectedCondition<Boolean> init(WebElement pWebElement, String pAttribute, String pValue) {
                    this.webElement = pWebElement;
                    this.attribute = pAttribute;
                    this.value = pValue;

                    return this;
                }

                @Override
                public Boolean apply(WebDriver pWebDriver) {
                    return isAttributeValueEqual(webElement, attribute, value, false);
                }
            }.init(weal.webElement, pAttribute, pValue));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsAttributeValuePresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsElementClickable(By pLocator, long pTimeout) {
        return waitIsElementClickable((Object) pLocator, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to be
     * clickable.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement is clickable.
     */
    protected static boolean waitIsElementClickable(Object pObject, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.elementToBeClickable(weal.webElement));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementClickable()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsElementNotPresent(By pLocator, long pTimeout) {
        return waitIsElementNotPresent((Object) pLocator, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to not be
     * present.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement is not present.
     */
    protected static boolean waitIsElementNotPresent(Object pObject, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            if (weal.webElement == null) {
                return true;
            }

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if (weal.locator == null) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private By locator;

                private ExpectedCondition<Boolean> init(By pLocator) {
                    this.locator = pLocator;
                    return this;
                }

                @Override
                public Boolean apply(WebDriver pWebDriver) {
                    return !isElementPresent(locator);
                }
            }.init(getBy(weal.locator)));

            return true;
        } catch (NoSuchElementException nsee) {
            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementNotPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsElementPresent(By pLocator, long pTimeout) {
        return waitIsElementPresent((Object) pLocator, pTimeout);
    }

    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to be present.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement is present.
     */
    protected static boolean waitIsElementPresent(Object pObject, long pTimeout) {
        try {
            WebElementAndLocator weal = new BrowserDriver.WebElementAndLocator();

            try {
                weal = getWebElementAndLocator(pObject);
            } catch (NoSuchElementException nsee) {
                // Do nothing.
            }

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if (weal.locator == null) {
                if (weal.webElement != null) {
                    weal.locator = getWebElementLocator(weal.webElement);
                } else {
                    if (pObject instanceof By) {
                        String strBy = ((By) pObject).toString();
                        int posPeriod = strBy.indexOf(".");
                        int posColon = strBy.indexOf(":");

                        if ((posPeriod > -1) && (posPeriod < posColon)) {
                            String attribute = strBy.substring(posPeriod + 1, posColon);
                            String value = strBy.substring(posColon + 1).trim();

                            weal.locator = attribute + "=" + value;
                        }
                    }
                }
            }

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.presenceOfElementLocated(getBy(weal.locator)));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementPresent()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsElementNotVisible(By pLocator, long pTimeout) {
        return waitIsElementNotVisible((Object) pLocator, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to be visible.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement is not visible.
     */
    protected static boolean waitIsElementNotVisible(Object pObject, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            if (weal.webElement == null) {
                return true;
            }

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(new ExpectedCondition<Boolean>() {
                private WebElement webElement;

                private ExpectedCondition<Boolean> init(WebElement pWebElement) {
                    this.webElement = pWebElement;
                    return this;
                }

                @Override
                public Boolean apply(WebDriver pWebDriver) {
                    return !isElementVisible(webElement);
                }
            }.init(weal.webElement));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementNotVisible()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsElementVisible(By pLocator, long pTimeout) {
        return waitIsElementVisible((Object) pLocator, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to be visible.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement is visible.
     */
    protected static boolean waitIsElementVisible(Object pObject, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if ((weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(weal.locator)));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementVisible()

    // QUALENG-89 (mremillard).
    public static boolean waitIsElementsNotVisible(By pLocator, long pTimeout) {
        try {
            return waitForElementsNotVisible(pLocator, pTimeout);
        } catch (Exception e) {
            return false;
        }
    } // waitIsElementsNotVisible()

    // QUALENG-100 (mremillard): Overloaded/put back this method for backwards compatibility.
    public static boolean waitIsTextPresent(By pLocator, String pText, long pTimeout) {
        return waitIsTextPresent((Object) pLocator, pText, pTimeout);
    }

    // QUALENG-89 (mremillard).
    // QUALENG-94 (mremillard): This method was modified so that we could pass it a By "locator" OR a WebElement.
    /**
     * This methods expects a WebElement or a By "locator" (for a WebElement) as its first parameter and waits for the WebElement specified/found to contain the
     * specified text.
     * 
     * @param pObject - A WebElement or a By "locator".
     * @param pText - The text that we are waiting for the WebElement to contain.
     * @param pTimeout - The maximum number of seconds to wait.
     *
     * @return (boolean) - Whether or not the WebElement contains the text.
     */
    protected static boolean waitIsTextPresent(Object pObject, String pText, long pTimeout) {
        try {
            WebElementAndLocator weal = getWebElementAndLocator(pObject);

            // QUALENG-101 (mremillard): Attempting to insure that we have a locator.
            if ((weal.locator == null) && (weal.webElement != null)) {
                weal.locator = getWebElementLocator(weal.webElement);
            }

            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            wait.until(ExpectedConditions.textToBePresentInElementLocated(getBy(weal.locator), pText));

            return true;
        } catch (Exception e) {
            return false;
        }
    } // waitIsTextPresent()

    // QUALENG-94 (mremillard).
    /**
     * This method attempts to return a WebElement's "locator" ("id=SomeID", "name=SomeName", etc...).
     * 
     * @param pWebElement - A WebElement.
     * 
     * @return (String) - The WebElement's locator or null if none could be found.
     */
    public static String getWebElementLocator(WebElement pWebElement) {
        String locator = null;

        if (pWebElement != null) {
            // QUALENG-98 (mremillard): This methods was revamped to be case-insensitive.

            // Retrieving the WebElement's attributes.
            Map<String, String> attributes = getWebElementAttributes(pWebElement);

            if ((attributes != null) && !attributes.isEmpty()) {
                String[] possibleAttributes = { "id", "name", "xpath", "link", "partiallink", "css", "classname" };

                Set<String> keys = attributes.keySet();
                for (String aKey : keys) {
                    if (!StringUtils.isEmpty(aKey)) {
                        for (String aPossibleAttribute : possibleAttributes) {
                            if (aPossibleAttribute.equalsIgnoreCase(aKey)) {
                                locator = aPossibleAttribute + "=" + attributes.get(aKey);
                                break;
                            }
                        }
                    }

                    if (locator != null) {
                        break;
                    }
                }
            }
        }

        return locator;
    } // getWebElementLocator()

    // QUALENG-94 (mremillard).
    /**
     * This method receives a WebElement or a By "locator" (for a WebElement) and returns a WebElementAndLocator object.
     * 
     * @param pObject - a WebElement or a By "locator".
     * 
     * @return (WebElementAndLocator) - A WebElementAndLocator object.
     */
    private static WebElementAndLocator getWebElementAndLocator(Object pObject) {
        WebElementAndLocator weal = new BrowserDriver.WebElementAndLocator();

        if (pObject instanceof WebElement) {
            weal.webElement = (WebElement) pObject;

            // QUALENG-101 (mremillard): Retrieving the locator only when in debug mode.
            if (inDebugMode) {
                weal.locator = getWebElementLocator(weal.webElement);
            }
        } else if (pObject instanceof By) {
            weal.webElement = getCurrentDriver().findElement((By) pObject);

            String strBy = ((By) pObject).toString();
            int posPeriod = strBy.indexOf(".");
            int posColon = strBy.indexOf(":");

            if ((posPeriod > -1) && (posPeriod < posColon)) {
                String attribute = strBy.substring(posPeriod + 1, posColon);
                String value = strBy.substring(posColon + 1).trim();

                weal.locator = attribute + "=" + value;
            } else {
                // QUALENG-101 (mremillard): Retrieving the locator only when in debug mode.
                if (inDebugMode) {
                    weal.locator = getWebElementLocator(weal.webElement);
                }
            }
        }

        return weal;
    } // getWebElementAndLocator()

    // QUALENG-98 (mremillard).
    /**
     * This method makes use of Javascript to retrieve a WebElements list of attributes.
     * 
     * @param pObject - a WebElement or a By "locator".
     * 
     * @return (Map<String, String>) - The WebElement's attributes.
     */
    public static Map<String, String> getWebElementAttributes(Object pObject) {
        Map<String, String> attributes = null;

        if (pObject != null) {
            WebElement webElement = null;

            if (pObject instanceof WebElement) {
                webElement = (WebElement) pObject;
            } else if (pObject instanceof By) {
                webElement = getCurrentDriver().findElement((By) pObject);
            }

            if (webElement != null) {
                String javascriptCode = "var items = {}; " + "var attributes = arguments[0].attributes; " + "for (var i = 0; i < attributes.length; i++) "
                        + "{ " + "   items[attributes[i].name] = attributes[i].value; " + "} " + "return items;";

                attributes = (Map<String, String>) javaScriptExecute(webElement, javascriptCode);
            }
        }

        return attributes;
    } // getWebElementAttributes()

    // QUALENG-96 (mremillard).
    /**
     * This method will wait a maximum number of seconds for a WebElement to be clickable. If it is then the WebElement will be returned. Otherwise null will be
     * returned.
     * 
     * @param pLocator - A By locator representing the WebElement that we want.
     * @param pTimeout - The maximum number of seconds to wait.
     * 
     * @return (WebElement) - The WebElement waited for or null if it was not clickable.
     */
    public static WebElement waitForAndGetElementWhenClickable(By pLocator, long pTimeout) {
        // WebElement webElement = waitForAndGetElementWhenPresent(pLocator, pTimeout);

        // if (webElement != null)
        // {
        // if (!waitForElementClickable(webElement, 1))
        // {
        // webElement = null;
        // }
        // }
        WebElement webElement = null;

        try {
            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            webElement = wait.until(ExpectedConditions.elementToBeClickable(pLocator));

        } catch (Exception e) {
            handleException(e, "waitForAndGetElementWhenClickable");
            // Do nothing.

        }

        return webElement;
    } // waitForAndGetElementWhenClickable()

    // QUALENG-96 (mremillard).
    /**
     * This method will wait a maximum number of seconds for a WebElement to be present. If it is then the WebElement will be returned. Otherwise null will be
     * returned.
     * 
     * @param pLocator - A By locator representing the WebElement that we want.
     * @param pTimeout - The maximum number of seconds to wait.
     * 
     * @return (WebElement) - The WebElement waited for or null if it was not present.
     */
    public static WebElement waitForAndGetElementWhenPresent(By pLocator, long pTimeout) {
        WebElement webElement = null;

        // boolean present = false;
        try {
            WebDriverWait wait = new WebDriverWait(getCurrentDriver(), pTimeout);
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(pLocator));

            // present = true;
        } catch (Exception e) {
            handleException(e, "waitForAndGetElementWhenPresent");
            // Do nothing.

        }

        // if (present)
        // {
        // webElement = getCurrentDriver().findElement(pLocator);
        // }

        return webElement;
    } // waitForAndGetElementWhenPresent()

    // QUALENG-101 (mremillard).
    /**
     * This method allows the user to set the "inDebugMode" flag.
     * 
     * @param pInDebugMode - True or false.
     */
    public static void setDebugMode(boolean pInDebugMode) {
        inDebugMode = pInDebugMode;
    } // setDebugMode()

    // QUALENG-103 (mremillard).
    /**
     * This method attempts to return a By's "locator" ("id=SomeID", "name=SomeName", etc...).
     * 
     * @param pByElement - A By element.
     * 
     * @return (String) - The By's locator or null if none could be found.
     */
    public static String getByLocator(By pByElement) {
        String locator = null;

        String strBy = pByElement.toString();
        int posPeriod = strBy.indexOf(".");
        int posColon = strBy.indexOf(":");

        if ((posPeriod > -1) && (posPeriod < posColon)) {
            String attribute = strBy.substring(posPeriod + 1, posColon);
            String value = strBy.substring(posColon + 1).trim();

            locator = attribute + "=" + value;
        }

        return locator;
    } // getByLocator()
}