package com.studyblue.bdd.stepdefs.grid;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studyblue.qa.selenium.commons.BrowserDriver;
import com.studyblue.qa.selenium.commons.GridFactoryException;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GridSteps {
    private static final Logger log = LoggerFactory.getLogger(BrowserDriver.class);
    private String url;
    private Class<? extends Throwable> expectedException;

    @Before
    public void init() {
        expectedException = null; // reset container for expected exception

    }

    @Before
    public void prepareTest(Scenario scenario) {
        if (scenario != null) {
            BrowserDriver.setScenario(scenario);
            log.info("@BEFORE: " + scenario.getName());
        }
    }

    @After
    public void validateExpectedExceptionNeverDetected() throws Exception {
        // detect if the expected exception is never catch in the test
        if (expectedException != null) {
            log.error("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            throw new Exception("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            // assertTrue(false);
        }
    }

    @Given("^I will use the grid without node")
    public void i_will_use_the_grid_without_node() throws Throwable {
        BrowserDriver.setBrowserName("chrome");
        BrowserDriver.setWhereToRun("grid");
        // set a default grid
        BrowserDriver.setListOfGridHub(Arrays.asList("http://172.16.25.80:4440/wd/hub"));
    }

    @Given("^I will use the grid: \"([^\"]*)\"$")
    public void i_will_use_the_grid(String pHubList) throws Throwable {
        BrowserDriver.setBrowserName("chrome");
        BrowserDriver.setWhereToRun("grid");
        // set a default grid
        BrowserDriver.setListOfGridHub(Arrays.asList(pHubList));
    }

    @Given("^I'm on logged on the TM360 \\(IJASH1\\) page$")
    public void i_m_on_logged_on_the_TM_IJASH_page() throws Throwable {
        long startTime = System.currentTimeMillis();

        // BrowserDriver.setPathToChrome("C:\\agent\\drivers\\Chrome\\chromedriver.exe");
        // BrowserDriver.setWhereToRun("local");

        BrowserDriver.setBrowserName("chrome");
        BrowserDriver.setWhereToRun("grid");
        // BrowserDriver.setListOfGridHub(Arrays.asList("http://172.16.25.80:4444/wd/hub")); // Quebec
        BrowserDriver.setListOfGridHub(Arrays.asList("http://10.1.210.77:4444/wd/hub")); // LA primary
        // BrowserDriver.setListOfGridHub(Arrays.asList("http://10.1.200.233:4444/wd/hub")); // LA secondary
        BrowserDriver.setTimeoutInSeconds(5);

        BrowserDriver.loadPage("https://tm360.corp/");
        // login
        BrowserDriver.secureType(BrowserDriver.getBy("id=username"), "JEMAutomatedTest");
        BrowserDriver.secureType(BrowserDriver.getBy("id=password"), "Avalanche8!");
        BrowserDriver.click(BrowserDriver.getBy("id=signin"));
        // wait end of loading page
        BrowserDriver.waitForPageLoad();
        // logout
        BrowserDriver.click(BrowserDriver.getBy("id=settings-icon"));
        BrowserDriver.click(BrowserDriver.getBy("classname=link-signout"));

        BrowserDriver.close();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        log.info("*********Execution time: " + elapsedTime / 1000 + " seconds.");
    }

    @When("^I'm on the google page$")
    public void i_m_on_the_google_page() throws Throwable {
        BrowserDriver.setBrowserName("firefox");
        BrowserDriver.setWhereToRun("local");
        log.info("I open the page: " + "http://www.google.com");
        try {
            BrowserDriver.loadPage("http://www.google.com");
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    @When("^I typee \"([^\"]*)\" in locator \"([^\"]*)\"$")
    public void i_typee(String pText, String pLocator) throws Throwable {
        log.info("Type [" + pText + "] in locator [" + pLocator + "]");
        try {
            BrowserDriver.type(BrowserDriver.getBy(pLocator), pText);
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    @Then("^I expect a GridFactoryException with message that contain \"(.*)\"$")
    public void i_expect_a_GridFactoryException_with_message_contain(String pMessage) {
        expectedException = new GridFactoryException(pMessage).getClass();
    }

    @Then("^the expected exception occurs$")
    public void the_expected_exception_occurs() throws Exception {
        // detect if the expected exception is never catch in the test
        if (expectedException != null) {
            log.error("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            expectedException = null; // reset container
            assertTrue(false);

        }
    }

    @Then("^the page title contains \"(.*?)\"$")
    public void the_page_title_contains(String pText) throws Throwable {
        String title = BrowserDriver.getTitle();
        log.info("Validate if title of the page [" + title + "] start with [" + pText + "]");
        assertTrue(title.contains(pText));
    }

    @Then("^thee page title start with \"(.*?)\"$")
    public void thee_page_title_start_with(String pText) throws Throwable {
        String title = BrowserDriver.getTitle();
        log.info("Validate if title of the page [" + title + "] start with [" + pText + "]");
        assertTrue(title.startsWith(pText));
    }

    @Then("^I take a snapshot$")
    public void i_take_a_snapshot() {
        BrowserDriver.screenShot("");
    }

    @Then("^I close the browser$")
    public void i_close_the_browser() {
        BrowserDriver.close();
    }

    public static void displayHtmlResult() {
        openUrl("file:///C:/dev/Workspaces/Default/selenium-commons/target/results/index.html");
    }

    /**
     * Open Url on the default browser of the computer
     * 
     * @param pUrl (String) the url to open
     * @return (boolean) return true if success.
     */
    private static boolean openUrl(String pUrl) {
        boolean result = false;
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                // Escaping all space characters.
                pUrl = pUrl.replaceAll("\\s", "%20");

                URI uri = new URI(pUrl);
                desktop.browse(uri);

                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean isAnExpectedException(Exception pException) {
        if (pException.getCause().getClass().equals(expectedException)) {
            expectedException = null; // reset to detect unused expected exception
            return true;
        } else if (pException.getCause().getCause().getClass().equals(expectedException)) // retry with sub cause
        {
            expectedException = null; // reset to detect unused expected exception
            return true;
        } else {

            return false;

        }
    }

    private void throwExceptionIfNotExpected(Exception pException) throws Exception {
        if (!isAnExpectedException(pException)) {
            throw pException;
        }
    }

}