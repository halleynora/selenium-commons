package com.studyblue.qa.selenium.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger; //QUALENG-22 (DPA) change logger class
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class GridFactory {

    private final Logger logger = LoggerFactory.getLogger(GridFactory.class);
    private static final String HUB_URL_PRIMARY = "http://192.168.99.100:32774/wd/hub"; // New grid address
    private static final Integer TIMEOUT_SECONDS = 120;
    private static List<String> listOfHub;
    private static DesiredCapabilities defaultCapabilities = new DesiredCapabilities();
    private static String currentHub; // the current hub url (used by BrowserDriver.EmbedVideo())


    /**
     * Constructor with the default primary and secondary hub
     */
    public GridFactory() {
        listOfHub = new ArrayList<String>();
        listOfHub.add(HUB_URL_PRIMARY);
        logger.info("GridFactory will be use with default hub list: [" + listOfHub + "].");
        // set default capabilities
        addDefaultCapabilities();
    }

    /**
     * Constructor with custom list of grid hub.
     *
     * @param pListOfHub (List<String>)
     */
    public GridFactory(List<String> pListOfHub) {
        // Validate the list of hub and remove bad instance of the list
        listOfHub = validateAndCleanHubList(pListOfHub);
        // if custom list is empty, set the default hub list
        if (listOfHub.isEmpty()) {
            logger.info("The custom hub list was empty, set the default list of hub.");
            listOfHub.add(HUB_URL_PRIMARY);
        }
        logger.info("GridFactory will be use with custom hub list: [" + listOfHub + "].");
        // set default capabilities
        addDefaultCapabilities();
    }

    /**
     * This method test each hub and remove the bad hub definition
     *
     * @param pListOfHub (List<String>)
     * @return (List<String>)
     */
    private List<String> validateAndCleanHubList(List<String> pListOfHub) {
        List<String> result = new ArrayList<String>();
        String hub;
        for (Iterator<String> it = pListOfHub.iterator(); it.hasNext();) {
            hub = it.next();
            try {
                URL url = new URL(hub);
                // add valid hub
                result.add(hub);
            } catch (MalformedURLException e) {
                logger.warn("The hub [" + hub + "]  was reject of the hub list, because it's not valid.");
            }
        }

        return result;
    }

    /**
     * Add the common capability to custom capabilities
     */
    private void addDefaultCapabilities()
    {
        defaultCapabilities.setCapability("takeScreenshot", true);
        // get the Jenkins build tag
        String jenkinsBuildTag = System.getenv("BUILD_TAG");
        if (jenkinsBuildTag != null && !jenkinsBuildTag.isEmpty()) {
            defaultCapabilities.setCapability("jenkinsBuildTag", jenkinsBuildTag);
        }
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            defaultCapabilities.setCapability("hostName", hostName);
        } catch (Exception e) {
            logger.info("Unable to get the Hostname");
        }

    }

    /**
     * This method will try to provide a Webdriver with the list of grid hub. The the first hub is not available, the code will fallback on the next hub of the
     * list, until the end of the list.
     *
     * @param capability (DesiredCapabilities)
     * @return (WebDriver) If problem return null.
     * @throws Exception
     */
    private WebDriver getBrowser(DesiredCapabilities pCapability) throws GridFactoryException {
        WebDriver driver = null;
        ExecutorService executor = Executors.newCachedThreadPool();
        String hubUrl;
        Callable<Object> task;
        Future<Object> future;
        Iterator<String> it = listOfHub.iterator();
        // add default capability
        DesiredCapabilities capability = pCapability;
        capability.merge(defaultCapabilities);
        logger.info("Set capability: " + capability);
        Exception lastException = null;

        // until the Webdriver is not instanciate, and the list of fallback is available.
        while (driver == null && it.hasNext()) {
            hubUrl = it.next();
            hubUrl = hubUrl.trim(); // DPA 2015/02/05 to prevent space in list of hub
            task = new BrowserCreate(capability, hubUrl);
            future = executor.submit(task);

//            try {
//                driver = new RemoteWebDriver(new URL(hubUrl),capability);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
            try {
                driver = (WebDriver) future.get(GridFactory.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                lastException = e;
                logger.warn("TimeoutException" + e.getMessage());
            } catch (InterruptedException e) {
                lastException = e;
                logger.warn(e.getMessage());
            } catch (ExecutionException e) {
                lastException = e;
                logger.warn(e.getMessage());
            }

            // TODO DPA change print
            if (driver == null) {
                if (it.hasNext()) {
                    logger.warn("WebDriver is null, unable to init the Webdriver with the grid hub: [" + hubUrl
                            + "] switch to next backup Grid. Alerting team.");
                } else {
                    logger.error("WebDriver is null, unable to init the Webdriver with the grids list: " + listOfHub + ".");
                    if (lastException == null) {
                        throw new GridFactoryException("Unable to init the Webdriver with the grids list: [" + listOfHub + "].");
                    } else {
                        throw new GridFactoryException("Unable to init the Webdriver with the grids list: [" + listOfHub + "].", lastException);
                    }
                }
            } else {
                logger.info("WebDriver was propertly setup on the grid hub: [" + hubUrl + "]");
                currentHub = hubUrl;
            }

        }

        return driver;
    }

    /**
     * Get a Safari instance
     *
     * @return (WebDriver)
     * @throws GridFactoryException
     */
    public WebDriver getSafariInstance() throws GridFactoryException {

        DesiredCapabilities capability = DesiredCapabilities.safari();
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return getBrowser(capability);
    }

    public WebDriver getInternetExplorerInstance() throws GridFactoryException {

        DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
        // capability.setPlatform(Platform.WINDOWS); // remove because already define by previous step
        // capability.setBrowserName("internet explorer"); //remove because already define by previous step
        capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        return getBrowser(capability);
    }

    public WebDriver getFirefoxInstance() throws GridFactoryException {

        return getFirefoxInstance(null);

    }

    /**
     * Return a WebDriver for FireFox with pProfile If the pProfile is null set the capability with a new empty Profile
     *
     * @param pProfile (FirefoxProfile)
     * @return (WebDriver)
     * @throws Exception
     * @author Danny.Paradis
     */
    public WebDriver getFirefoxInstance(FirefoxProfile pProfile) throws GridFactoryException {
        FirefoxProfile profile = new FirefoxProfile();
        if (pProfile != null) {
            profile = pProfile;
        }
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        // capability.setCapability("takeScreenshot", true); //already set by default capability
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); // TODO DPA move to default capability
        capability.setCapability(FirefoxDriver.PROFILE, profile);
        return getBrowser(capability);

    }

    public WebDriver getChromeInstance() throws GridFactoryException {
        return getChromeInstance(null);
    }

    /**
     * Return a WebDriver for Chrome with pOption If pOtions is null set the capability with a new empty Options
     *
     * @param pOtions (ChromeOptions)
     * @return (WebDriver)
     * @throws Exception
     */
    public WebDriver getChromeInstance(ChromeOptions pOtions) throws GridFactoryException {
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();
        if (pOtions != null) {
            options = pOtions;
        }

        options.addArguments("--start-maximized");

        capability.setCapability(ChromeOptions.CAPABILITY, options);
        // capability.setCapability("takeScreenshot", true); already set in default capability
        return getBrowser(capability);
    }

    public WebDriver getPhantomJSInstance() throws GridFactoryException {
        DesiredCapabilities capability = DesiredCapabilities.phantomjs();
        return getBrowser(capability);
    }

    public class BrowserCreate implements Callable<Object> {
        private final DesiredCapabilities capability;
        private final String hubUrl;

        public BrowserCreate(DesiredCapabilities capability, String hubUrl) {
            this.capability = capability;
            this.hubUrl = hubUrl;
        }

        @Override
        public Object call() throws MalformedURLException {
            return new RemoteWebDriver(new URL(hubUrl), capability);
        }

    }

    public String getCurrentHub() {
        return currentHub;
    }

    /**
     * Simple http get
     *
     * @param pUrl (String)
     * @return (String) response
     */
    private String httpGet(String pUrl) {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(pUrl);

            logger.debug("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            logger.debug(responseBody);
            result = responseBody;
        } catch (IOException e) {
            logger.error("httpGet error", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {

                logger.error("httpGet error", e);
            }
        }
        return result;
    }

    /**
     * Return the node ip where the test is executed.
     *
     * @param pSessionId (String)
     * @return (String)
     */
    public String getNodeIp(String pSessionId) {
        String node = null;

        try {
            URL hubUrl = new URL(getCurrentHub());

            String request = "http://" + hubUrl.getHost() + ":" + hubUrl.getPort() + "/grid/api/testsession?session=" + pSessionId;
            String response = httpGet(request);
            // {"msg":"slot found !","success":true,"session":"0f51ff1a-23fc-4abd-970f-bf740bebb934","internalKey":"134c7b56-fab1-46a0-9027-9e2d79fc9100","inactivityTime":188,"proxyId":"http://172.16.25.81:5555"}
            if (response != null) {
                // extract the proxyId info
                JsonParser jsonParser = new JsonParser();
                JsonObject jo = (JsonObject) jsonParser.parse(response);
                if (jo != null) {
                    node = jo.get("proxyId").getAsString();
                }
            }

        } catch (MalformedURLException e1) {
            logger.error("Bad Hub url!", e1);

        }
        return node;
    }


}
