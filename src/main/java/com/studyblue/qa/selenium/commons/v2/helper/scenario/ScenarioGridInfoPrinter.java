package com.studyblue.qa.selenium.commons.v2.helper.scenario;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import com.studyblue.qa.selenium.commons.v2.helper.WebDriverUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cucumber.api.Scenario;

public class ScenarioGridInfoPrinter extends ScenarioHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioGridInfoPrinter.class);

    private final WebDriver webDriver;

    public ScenarioGridInfoPrinter(Supplier<Scenario> scenarioSuplier, WebDriver webDriver) {
        super(scenarioSuplier);
        this.webDriver = checkNotNull(webDriver);
    }

    /**
     * Embed in the cucumber scenario the Grid hub, the grid info Hub, node, session-id and capability
     * 
     * @param scenario
     */
    public void printGridInfo() {
        if (getUnwrappedWebDriver() instanceof RemoteWebDriver) {
            Optional<URL> hubURL = getHubUrl();

            String hub = "<b>Hub:</b> <span id=hub>" + (hubURL.isPresent() ? hubURL.get() : "UNKNOWN") + "</span><br>";
            String nodeIp = "<b>Node:</b> ";
            String sessionInfo = "<b>Session id:</b> <span id=sessionid>" + getSessionId() + "</span><br>";
            String capability = "<b>Capabilities:</b> <span id=capabilities>" + getCapabilities() + "</span><br>";
            if (hubURL.isPresent()) {
                nodeIp = "<b>Node:</b> <span id=node>" + getNodeIp(getSessionId(), hubURL.get()) + "</span><br>";
            }
            printToggleText("Grid info:", hub + nodeIp + sessionInfo + capability);
        } else {
            LOGGER.debug("Cannot print GridInfo, we are not a remote webdriver");
        }
    }
    
    public void printGridInfoToLog() {
        if (getUnwrappedWebDriver() instanceof RemoteWebDriver) {
            Optional<URL> hubURL = getHubUrl();

            String hub = "hub:[" + (hubURL.isPresent() ? hubURL.get() : "UNKNOWN") + "]";
            String nodeIp = "";
            String sessionInfo = ", sessionId:[" + getSessionId() + "]";
            String capability = ", capabilities:[" + getCapabilities() + "]";
            if (hubURL.isPresent()) {
                nodeIp = ", nodeIp:[" + getNodeIp(getSessionId(), hubURL.get()) + "]";
            }
            LOGGER.info("Grid info: {} {} {} {}", hub , nodeIp , sessionInfo , capability);
        } else {
            LOGGER.debug("Cannot print GridInfo, we are not a remote webdriver");
        }

    }

    private Optional<URL> getHubUrl() {
        WebDriver webDriver = getUnwrappedWebDriver();
        if (webDriver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver) webDriver;
            if (remoteWebDriver.getCommandExecutor() instanceof HttpCommandExecutor) {
                return Optional.of(((HttpCommandExecutor) remoteWebDriver.getCommandExecutor()).getAddressOfRemoteServer());
            }
        }
        return Optional.absent();
    }

    protected WebDriver getUnwrappedWebDriver() {
        return WebDriverUtil.getUnwrappedWebDriver(webDriver);
    }

    /**
     * Return the selenium-server session id
     * 
     * @return (String)
     */
    public String getSessionId() {
        WebDriver webDriver = getUnwrappedWebDriver();
        if (webDriver instanceof RemoteWebDriver) {
            return ((RemoteWebDriver) webDriver).getSessionId().toString();
        }
        return "UNKNOWN";
    }

    /**
     * Return the RemoteDriver capabilities
     * 
     * @return (String)
     */
    public String getCapabilities() {
        WebDriver webDriver = getUnwrappedWebDriver();
        if (webDriver instanceof HasCapabilities) {
            return ((HasCapabilities) webDriver).getCapabilities().toString();
        }
        return "";
    }

    /**
     * Simple http get
     * 
     * @param url (String)
     * @return (String) response
     */
    private String httpGet(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);

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
            return httpclient.execute(httpget, responseHandler);
        } finally {
            IOUtils.closeQuietly(httpclient);
        }
    }

    /**
     * Return the node ip where the test is executed.
     * 
     * @param sessionId (String)
     * @return (String)
     */
    private String getNodeIp(String sessionId, URL hubUrl) {
        String request = "http://" + hubUrl.getHost() + ":" + hubUrl.getPort() + "/grid/api/testsession?session=" + sessionId;
        try {
            String response = httpGet(request);
            // {"msg":"slot found
            // !","success":true,"session":"0f51ff1a-23fc-4abd-970f-bf740bebb934","internalKey":"134c7b56-fab1-46a0-9027-9e2d79fc9100","inactivityTime":188,"proxyId":"http://172.16.25.81:5555"}
            // extract the proxyId info
            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject) jsonParser.parse(response);
            return jo.get("proxyId").getAsString();
        } catch (IOException e) {
            LOGGER.debug("Cannot get nodeIp with url [" + request + "]", e);
        }

        return "UNKNOWN";
    }

}
