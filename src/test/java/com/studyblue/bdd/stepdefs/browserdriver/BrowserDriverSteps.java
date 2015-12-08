package com.studyblue.bdd.stepdefs.browserdriver;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.studyblue.qa.selenium.commons.BrowserDriver;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BrowserDriverSteps {
    private static final Logger log = LoggerFactory.getLogger(BrowserDriver.class);
    private String url;
    private Class<? extends Throwable> expectedException;

    // ****************************************************************************************************
    // The @Before methods.
    // ****************************************************************************************************
    @Before
    public void init() {
        expectedException = null; // reset container for expected exception
        BrowserDriver.setBrowserName("chrome");
        // BrowserDriver.setBrowserName("chrome_emulator: Apple iPad 3 / 4");
        // BrowserDriver.setBrowserName("chrome_emulator: Apple iPhone 6");
        // BrowserDriver.setBrowserName("chrome_emulator: test");
        BrowserDriver.setWhereToRun("grid");
        // BrowserDriver.setWhereToRun("localgrid");
        BrowserDriver.setPathToChrome("C:\\agent\\drivers\\Chrome\\chromedriver.exe");
        BrowserDriver.setTimeoutInSeconds(5);
    }

    @Before
    public void prepareTest(Scenario scenario) {
        if (scenario != null) {
            BrowserDriver.setScenario(scenario);
            log.info("@BEFORE: " + scenario.getName());
        }
    }

    // ****************************************************************************************************
    // The @After methods.
    // ****************************************************************************************************
    @After
    public void validateExpectedExceptionNeverDetected() throws Exception {
        // detect if the expected exception is never catch in the test
        if (expectedException != null) {
            log.error("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            throw new Exception("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            // assertTrue(false);
        }
    }

    // ****************************************************************************************************
    // The @Given methods.
    // ****************************************************************************************************
    @Given("^I'm on smoketest page$")
    public void i_m_on_smoketest_page() throws Throwable {
        url = "http://aso1.mf.qa401.coresys.tmcs/dpa/help/SmokeTests.html";
        BrowserDriver.loadPage(url, "Smoke Tests");
    }

    // ****************************************************************************************************
    // The @When methods.
    // ****************************************************************************************************
    @When("^I type \"([^\"]*)\" in locator \"([^\"]*)\"$")
    public void i_type(String pText, String pLocator) throws Throwable {
        log.info("Type [" + pText + "] in locator [" + pLocator + "]");
        try {
            BrowserDriver.type(BrowserDriver.getBy(pLocator), pText);
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    @When("^I click on locator \"(.*?)\"$")
    public void i_click_on_locator(String pLocator) throws Throwable {
        try {
            BrowserDriver.click(BrowserDriver.getBy(pLocator));
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    @When("^I clear value in locator \"(.*?)\"$")
    public void i_clear_value_in_locator(String pLocator) throws Throwable {
        log.info("I clear value in locator [" + pLocator + "]");
        try {
            BrowserDriver.clear(BrowserDriver.getBy(pLocator));
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    @When("^I take a snapshot$")
    public void i_take_a_snapshot() {
        BrowserDriver.screenShot("");
    }

    @When("^I expect an ElementNotVisibleException exception$")
    public void i_expect_an_elementNotVisibleException_exception() {
        expectedException = ElementNotVisibleException.class;
    }

    @When("^I expect an InvalidElementStateException exception$")
    public void i_expect_an_invalidElementStateException_exception() {
        expectedException = InvalidElementStateException.class;
    }

    @When("^I expect an TimeoutException exception$")
    public void i_expect_an_timeoutException_exception() {
        expectedException = TimeoutException.class;
    }

    @When("^I expect a NoSuchElementException exception$")
    public void i_expect_an_noSuchElementException_exception() {
        expectedException = NoSuchElementException.class;
    }

    @When("^I test the Wait for Element clickable with locator \"(.*?)\"$")
    public void i_test_the_Wait_for_Element_clickable(String pLocator) throws Throwable {
        BrowserDriver.waitForElementClickable(BrowserDriver.getBy(pLocator));

    }

    @When("^I mouseOver with locator \"(.*?)\"$")
    public void i_mouseOver_with_locator(String pLocator) throws Throwable {
        BrowserDriver.mouseOver(BrowserDriver.getBy(pLocator));
    }

    @When("^I use javascript to mouseOver with locator \"(.*?)\"$")
    public void i_use_javascript_mouseOver_with_locator(String pLocator) throws Throwable {
        BrowserDriver.mouseOverJS(BrowserDriver.getBy(pLocator));
    }

    // (mremillard): Selecting 1 or more <select> element options by value.
    // QUALENG-94 (mremillard): This test was enhanced to test selectDropdownByValue() by passing it both a By "locator"
    // and a WebElement.
    @When("^I select options using values \"(.*?)\" in locator \"(.*?)\"$")
    public void select_options_by_value(String pOptionValues, String pLocator) throws Throwable {
        String[] values = pOptionValues.split(",");

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        for (String anOptionValue : values) {
            if (anOptionValue != null) {
                anOptionValue = anOptionValue.trim();
                if (!anOptionValue.equals("")) {
                    BrowserDriver.selectDropdownByValue(byElement, anOptionValue);
                }
            }
        }

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        for (String anOptionValue : values) {
            if (anOptionValue != null) {
                anOptionValue = anOptionValue.trim();
                if (!anOptionValue.equals("")) {
                    BrowserDriver.selectDropdownByValue(webElement, anOptionValue);
                }
            }
        }
    } // select_options_by_value()

    // (mremillard): Selecting 1 or more <select> element options by index
    // QUALENG-94 (mremillard): This test was enhanced to test selectDropdownByIndex() by passing it both a By "locator"
    // and a WebElement.
    @When("^I select options using indexes \"(.*?)\" in locator \"(.*?)\"$")
    public void select_options_by_index(String pOptionIndexes, String pLocator) throws Throwable {
        String[] indexes = pOptionIndexes.split(",");

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        for (String anIndex : indexes) {
            if (!StringUtils.isEmpty(anIndex)) {
                int index = -1;
                try {
                    index = Integer.valueOf(anIndex.trim()).intValue();
                } catch (Exception e) {
                    // Do nothing.
                }

                if (index > -1) {
                    BrowserDriver.selectDropdownByIndex(byElement, index);
                } else {
                    log.warn("select_options_by_index(): The specified indexes are not valid: " + pOptionIndexes);
                }
            }
        }

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        for (String anIndex : indexes) {
            if (!StringUtils.isEmpty(anIndex)) {
                int index = -1;
                try {
                    index = Integer.valueOf(anIndex.trim()).intValue();
                } catch (Exception e) {
                    // Do nothing.
                }

                if (index > -1) {
                    BrowserDriver.selectDropdownByIndex(webElement, index);
                } else {
                    log.warn("select_options_by_index(): The specified indexes are not valid: " + pOptionIndexes);
                }
            }
        }
    } // select_options_by_index()

    // (mremillard): Selecting 1 or more <select> element options by visible text
    // QUALENG-94 (mremillard): This test was enhanced to test selectDropdownByVisibleText() by passing it both a
    // By "locator" and a WebElement.
    @When("^I select options using visible text \"(.*?)\" in locator \"(.*?)\"$")
    public void select_options_by_visible_text(String pOptionVisibleText, String pLocator) throws Throwable {
        String[] values = pOptionVisibleText.split(",");

        // Testing with a By "locator".
        By byElement = BrowserDriver.getBy(pLocator);
        for (String anOptionVisibleText : values) {
            if (anOptionVisibleText != null) {
                anOptionVisibleText = anOptionVisibleText.trim();
                if (!anOptionVisibleText.equals("")) {
                    BrowserDriver.selectDropdownByVisibleText(byElement, anOptionVisibleText);
                }
            }
        }

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        for (String anOptionVisibleText : values) {
            if (anOptionVisibleText != null) {
                anOptionVisibleText = anOptionVisibleText.trim();
                if (!anOptionVisibleText.equals("")) {
                    BrowserDriver.selectDropdownByVisibleText(webElement, anOptionVisibleText);
                }
            }
        }
    } // select_options_by_visible_text()

    // QUALENG-90 (mremillard): Testing method BrowserDriver.waitForElementsNotVisible().
    @When("^I test method waitForElementsNotVisible with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_for_elements_not_visible(String pLocator, String pTimeout) {
        log.info("Validating that elements \"" + pLocator + "\" are not visible.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitForElementsNotVisible(BrowserDriver.getBy(pLocator), timeout);

        if (pLocator.contains("Invisible")) {
            assertTrue(result);
        } else {
            assertTrue(!result);
        }
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsAttributeNotPresent().
    @When("^I test method waitIsAttributeNotPresent with locator \"(.*?)\" attribute \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_attribute_not_present(String pLocator, String pAttribute, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" doesn't have the \"" + pAttribute + "\" attribute.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsAttributeNotPresent(BrowserDriver.getBy(pLocator), pAttribute, timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsAttributePresent().
    @When("^I test method waitIsAttributePresent with locator \"(.*?)\" attribute \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_attribute_present(String pLocator, String pAttribute, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" has the \"" + pAttribute + "\" attribute.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsAttributePresent(BrowserDriver.getBy(pLocator), pAttribute, timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsAttributeValueNotPresent().
    @When("^I test method waitIsAttributeValueNotPresent with locator \"(.*?)\" attribute \"(.*?)\" value \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_attribute_value_not_present(String pLocator, String pAttribute, String pValue, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\"'s \"" + pAttribute + "\" attribute doesn't contain \"" + pValue + "\".");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsAttributeValueNotPresent(BrowserDriver.getBy(pLocator), pAttribute, pValue, timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsAttributeValuePresent().
    @When("^I test method waitIsAttributeValuePresent with locator \"(.*?)\" attribute \"(.*?)\" value \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_attribute_value_present(String pLocator, String pAttribute, String pValue, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\"'s \"" + pAttribute + "\" attribute contains \"" + pValue + "\".");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsAttributeValuePresent(BrowserDriver.getBy(pLocator), pAttribute, pValue, timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementClickable().
    @When("^I test method waitIsElementClickable with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_element_clickable(String pLocator, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" is clickable.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsElementClickable(BrowserDriver.getBy(pLocator), timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementNotPresent().
    @When("^I test method waitIsElementNotPresent with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_element_not_present(String pLocator, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" is not present.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsElementNotPresent(BrowserDriver.getBy(pLocator), timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementPresent().
    @When("^I test method waitIsElementPresent with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_element_present(String pLocator, String pTimeout) {
        if (pLocator.toLowerCase().contains("invalid")) {
            log.info("Validating that element \"" + pLocator + "\" is not present.");
            long timeout = Integer.valueOf(pTimeout).longValue();
            boolean result = BrowserDriver.waitIsElementPresent(BrowserDriver.getBy(pLocator), timeout);
            assertTrue(!result);
        } else {
            log.info("Validating that element \"" + pLocator + "\" is present.");
            long timeout = Integer.valueOf(pTimeout).longValue();
            boolean result = BrowserDriver.waitIsElementPresent(BrowserDriver.getBy(pLocator), timeout);
            assertTrue(result);
        }
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementNotVisible().
    @When("^I test method waitIsElementNotVisible with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_element_not_visible(String pLocator, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" is not visible.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsElementNotVisible(BrowserDriver.getBy(pLocator), timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementVisible().
    @When("^I test method waitIsElementVisible with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_element_visible(String pLocator, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" is visible.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsElementVisible(BrowserDriver.getBy(pLocator), timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsElementsNotVisible().
    @When("^I test method waitIsElementsNotVisible with locator \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_elements_not_visible(String pLocator, String pTimeout) {
        log.info("Validating that elements \"" + pLocator + "\" are not visible.");
        long timeout = Integer.valueOf(pTimeout).longValue();
        boolean result = BrowserDriver.waitIsElementsNotVisible(BrowserDriver.getBy(pLocator), timeout);
        assertTrue(result);
    }

    // QUALENG-89 (mremillard): Testing method BrowserDriver.waitIsTextPresent().
    @When("^I test method waitIsTextPresent with locator \"(.*?)\" text \"(.*?)\" and timeout \"(.*?)\"$")
    public void wait_is_text_present(String pLocator, String pText, String pTimeout) {
        log.info("Validating that element \"" + pLocator + "\" contains \"" + pText + "\".");
        long timeout = Integer.valueOf(pTimeout).longValue();

        By elementBy = BrowserDriver.getBy(pLocator);
        WebElement element = BrowserDriver.getCurrentDriver().findElement(elementBy);
        element.sendKeys(pText);

        boolean result = BrowserDriver.waitIsTextPresent(elementBy, pText, timeout);
        assertTrue(result);
    }

    // QUALENG-94 (mremillard): Testing method BrowserDriver.sendKeys().
    @When("^I test methods clear, sendKeys and type with locator \"(.*?)\" and text \"(.*?)\"$")
    public void test_clear_sendKeys_type(String pLocator, String pText) {
        // Testing with a By locator.
        String text = " with a By locator.";
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.clear(byElement);
        BrowserDriver.type(byElement, text);
        BrowserDriver.sendKeys(byElement, Keys.HOME);
        BrowserDriver.type(byElement, pText);
        log.info("Validating that element \"" + pLocator + "\" contains \"" + pText + text + "\".");
        boolean result = BrowserDriver.getAttributeValue(byElement, "value").equals(pText + text);
        assertTrue(result);

        // Testing with a WebElement.
        text = "with a WebElement.";
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.clear(webElement);
        BrowserDriver.type(webElement, text);
        BrowserDriver.sendKeys(webElement, Keys.HOME);
        BrowserDriver.type(webElement, pText);
        log.info("Validating that element \"" + pLocator + "\" contains \"" + pText + text + "\".");
        result = BrowserDriver.getAttributeValue(byElement, "value").equals(pText + text);
        assertTrue(result);
    } // clear_sendKeys_type()

    // QUALENG-94 (mremillard): Testing BrowserDriver methods clear() and secureType().
    @When("^I test methods clear and secureType with locator \"(.*?)\" and text \"(.*?)\"$")
    public void test_clear_secureType(String pLocator, String pText) {
        // Testing with a By locator.
        String text = pText + " with a By locator.";
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.clear(byElement);
        BrowserDriver.secureType(byElement, text);
        log.info("Validating that element \"" + pLocator + "\" contains \"" + text + "\".");
        boolean result = BrowserDriver.getAttributeValue(byElement, "value").equals(text);
        assertTrue(result);

        // Testing with a WebElement.
        text = pText + " with a WebElement.";
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.clear(webElement);
        BrowserDriver.secureType(webElement, text);
        log.info("Validating that element \"" + pLocator + "\" contains \"" + text + "\".");
        result = BrowserDriver.getAttributeValue(byElement, "value").equals(text);
        assertTrue(result);
    } // test_clear_secureType()

    // QUALENG-94 (mremillard): Testing BrowserDriver method click().
    @When("^I test method click with locator \"(.*?)\"$")
    public void test_click(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        boolean initialCheckStatus = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue();
        // Calling waitClick(), which calls click(), too also test waitClick().
        BrowserDriver.waitClick(byElement, 3000);
        log.info("Validating that element \"" + pLocator + "\" has been clicked.");
        boolean result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() != initialCheckStatus;
        assertTrue(result);

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.click(webElement);
        result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() == initialCheckStatus;
        assertTrue(result);
    } // test_click()

    // QUALENG-94 (mremillard): Testing BrowserDriver method clickJS().
    @When("^I test method clickJS with locator \"(.*?)\"$")
    public void test_clickJS(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        boolean initialCheckStatus = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue();
        BrowserDriver.clickJS(byElement);
        log.info("Validating that element \"" + pLocator + "\" has been clicked.");
        boolean result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() != initialCheckStatus;
        assertTrue(result);

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.clickJS(webElement);
        result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() == initialCheckStatus;
        assertTrue(result);
    } // test_clickJS()

    // QUALENG-94 (mremillard): Testing BrowserDriver method scrollAndClick().
    @When("^I test method scrollAndClick with locator \"(.*?)\"$")
    public void test_scrollAndClick(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        boolean initialCheckStatus = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue();
        BrowserDriver.scrollAndClick(byElement);
        log.info("Validating that element \"" + pLocator + "\" has been clicked.");
        boolean result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() != initialCheckStatus;
        assertTrue(result);

        // Scrolling back up the page.
        BrowserDriver.sendKeys(byElement, Keys.PAGE_UP);

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.scrollAndClick(webElement);
        result = Boolean.valueOf(BrowserDriver.getAttributeValue(byElement, "checked")).booleanValue() == initialCheckStatus;
        assertTrue(result);
    } // test_scrollAndClick()

    // QUALENG-94 (mremillard): Testing BrowserDriver method scrollIntoView().
    @When("^I test method scrollIntoView with locator \"(.*?)\"$")
    public void test_scrollIntoView(String pLocator) {
        String id = pLocator.substring(pLocator.indexOf("=") + 1);
        String javascriptCode = "return (document.getElementById('" + id + "').getBoundingClientRect().top).toString();";

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.sendKeys(byElement, Keys.PAGE_UP);
        String top1 = (String) BrowserDriver.executeJavascript(javascriptCode);
        BrowserDriver.scrollIntoView(byElement);
        String top2 = (String) BrowserDriver.executeJavascript(javascriptCode);
        assertTrue(Double.valueOf(top1).intValue() > Double.valueOf(top2).intValue());

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.sendKeys(webElement, Keys.PAGE_UP);
        top1 = (String) BrowserDriver.executeJavascript(javascriptCode);
        BrowserDriver.scrollIntoView(webElement);
        top2 = (String) BrowserDriver.executeJavascript(javascriptCode);
        assertTrue(Double.valueOf(top1).intValue() > Double.valueOf(top2).intValue());
    } // test_scrollIntoView()

    // QUALENG-94 (mremillard): Testing BrowserDriver method isElementVisible().
    @When("^I test method isElementVisible with locator \"(.*?)\"$")
    public void test_isElementVisible(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.isElementVisible(byElement));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.isElementVisible(webElement));
    } // test_isElementVisible()

    // QUALENG-94 (mremillard): Testing BrowserDriver method isTextPresent().
    @When("^I test method isTextPresent with locator \"(.*?)\" and text \"(.*?)\"$")
    public void test_isTextPresent(String pLocator, String pText) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        boolean isPresent = BrowserDriver.isTextPresent(byElement, pText);
        assertTrue(isPresent);

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.isTextPresent(webElement, pText));
    } // test_isTextPresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method isAttributePresent().
    @When("^I test method isAttributePresent with locator \"(.*?)\"$")
    public void test_isAttributePresent(String pLocator) {
        String attribute = "value";

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.isAttributePresent(byElement, attribute, true));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.isAttributePresent(webElement, attribute, true));
    } // test_isAttributePresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method isAttributeValueEqual().
    @When("^I test method isAttributeValueEqual with locator \"(.*?)\" attribute \"(.*?)\" and text \"(.*?)\"$")
    public void test_isAttributeValueEqual(String pLocator, String pAttribute, String pValue) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.isAttributeValueEqual(byElement, pAttribute, pValue, true));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.isAttributeValueEqual(webElement, pAttribute, pValue, true));
    } // test_isAttributeValueEqual()

    // QUALENG-94 (mremillard): Testing BrowserDriver method getAttributeValue().
    @When("^I test method getAttributeValue with locator \"(.*?)\" attribute \"(.*?)\" and text \"(.*?)\"$")
    public void test_getAttributeValue(String pLocator, String pAttribute, String pValue) {
        String expectedValue = "CheckBox value";

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(expectedValue.equals(BrowserDriver.getAttributeValue(byElement, pAttribute)));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(expectedValue.equals(BrowserDriver.getAttributeValue(webElement, pAttribute)));
    } // test_getAttributeValue()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForElementClickable().
    @When("^I test method waitForElementClickable with locator \"(.*?)\"$")
    public void test_waitForElementClickable(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.waitForElementClickable(byElement, 3));

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // assertTrue(BrowserDriver.waitForElementClickable(webElement, 3));
    } // test_waitForElementClickable()

    // (DPA): Testing BrowserDriver method waitForElementClickable() with an element not present.
    @When("^I test method waitForElementClickable with a non present locator \"(.*?)\"$")
    public void test_waitForElementClickableNotPresent(String pLocator) throws Throwable {
        try {
            // Testing with a By locator.
            By byElement = BrowserDriver.getBy(pLocator);
            assertTrue(!BrowserDriver.waitForElementClickable(byElement, 3));
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    } // test_waitForElementClickableNotPresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForTextPresent().
    @When("^I test method waitForTextPresent with locator \"(.*?)\" and text \"(.*?)\"$")
    public void test_waitForTextPresent(String pLocator, String pText) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForTextPresent(byElement, pText, 3);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForTextPresent(webElement, pText, 3);
    } // test_waitForTextPresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForAttributePresent().
    @When("^I test method waitForAttributePresent with locator \"(.*?)\" and attribute \"(.*?)\"$")
    public void test_waitForAttributePresent(String pLocator, String pAttribute) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForAttributePresent(byElement, pAttribute, 3);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForAttributePresent(webElement, pAttribute, 3);
    } // test_waitForAttributePresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForAttributeNotPresent().
    @When("^I test method waitForAttributeNotPresent with locator \"(.*?)\" and attribute \"(.*?)\"$")
    public void test_waitForAttributeNotPresent(String pLocator, String pAttribute) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForAttributeNotPresent(byElement, pAttribute, 3);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForAttributeNotPresent(webElement, pAttribute, 3);
    } // test_waitForAttributeNotPresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForAttributeValuePresent().
    @When("^I test method waitForAttributeValuePresent with locator \"(.*?)\" attribute \"(.*?)\" and value \"(.*?)\"$")
    public void test_waitForAttributeValuePresent(String pLocator, String pAttribute, String pValue) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForAttributeValuePresent(byElement, pAttribute, pValue, 3);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForAttributeValuePresent(webElement, pAttribute, pValue, 3);
    } // test_waitForAttributeValuePresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method waitForAttributeValueNotPresent().
    @When("^I test method waitForAttributeValueNotPresent with locator \"(.*?)\" attribute \"(.*?)\" and value \"(.*?)\"$")
    public void test_waitForAttributeValueNotPresent(String pLocator, String pAttribute, String pValue) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForAttributeValueNotPresent(byElement, pAttribute, pValue, 3);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForAttributeValueNotPresent(webElement, pAttribute, pValue, 3);
    } // test_waitForAttributeValueNotPresent()

    // QUALENG-94 (mremillard): Testing BrowserDriver method doesTableHaveRows().
    @When("^I test method doesTableHaveRows with locator \"(.*?)\"$")
    public void test_doesTableHaveRows(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.doesTableHaveRows(byElement));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.doesTableHaveRows(webElement));
    } // test_doesTableHaveRows()

    // QUALENG-94 (mremillard): Testing BrowserDriver method rowsInTable().
    @When("^I test method rowsInTable with locator \"(.*?)\"$")
    public void test_rowsInTable(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.rowsInTable(byElement) == 4);

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.rowsInTable(webElement) == 4);
    } // test_rowsInTable()

    // QUALENG-94 (mremillard): Testing BrowserDriver method getText().
    @When("^I test method getText with locator \"(.*?)\" and expect \"(.*?)\"$")
    public void test_getText(String pLocator, String pText) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.getText(byElement).equals(pText));

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        assertTrue(BrowserDriver.getText(webElement).equals(pText));
    } // test_getText()

    // QUALENG-94 (mremillard): Testing BrowserDriver method moveToElement().
    @When("^I test method moveToElement with locator \"(.*?)\"$")
    public void test_moveToElement(String pLocator) {
        String id = pLocator.substring(pLocator.indexOf("=") + 1);
        String javascriptCode = "return (document.getElementById('" + id + "').getBoundingClientRect().top).toString();";

        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.sendKeys(byElement, Keys.PAGE_UP);
        String top1 = (String) BrowserDriver.executeJavascript(javascriptCode);
        BrowserDriver.moveToElement(byElement);
        String top2 = (String) BrowserDriver.executeJavascript(javascriptCode);
        assertTrue(Double.valueOf(top1).intValue() > Double.valueOf(top2).intValue());

        // Testing with a WebElement.
        WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        BrowserDriver.sendKeys(webElement, Keys.PAGE_UP);
        top1 = (String) BrowserDriver.executeJavascript(javascriptCode);
        BrowserDriver.moveToElement(webElement);
        top2 = (String) BrowserDriver.executeJavascript(javascriptCode);
        assertTrue(Double.valueOf(top1).intValue() > Double.valueOf(top2).intValue());
    } // test_moveToElement()

    // QUALENG-94 (mremillard): Testing BrowserDriver methods mouseOverJS() and mouseOutJS().
    @When("^I test methods mouseOverJS and mouseOutJS with locators \"(.*?)\" and \"(.*?)\"$")
    public void test_mouseOverJS_mouseOutJS(String pLocator1, String pLocator2) {
        // Testing with a By locator.
        By byElement1 = BrowserDriver.getBy(pLocator1);
        By byElement2 = BrowserDriver.getBy(pLocator2);
        assertTrue(!BrowserDriver.isElementVisible(byElement2));
        BrowserDriver.mouseOverJS(byElement1);
        assertTrue(BrowserDriver.isElementVisible(byElement2));
        BrowserDriver.mouseOutJS(byElement1);
        assertTrue(!BrowserDriver.isElementVisible(byElement2));

        // Testing with a WebElement.
        WebElement webElement1 = BrowserDriver.getCurrentDriver().findElement(byElement1);
        WebElement webElement2 = BrowserDriver.getCurrentDriver().findElement(byElement2);
        assertTrue(!BrowserDriver.isElementVisible(webElement2));
        BrowserDriver.mouseOverJS(webElement1);
        assertTrue(BrowserDriver.isElementVisible(webElement2));
        BrowserDriver.mouseOutJS(webElement1);
        assertTrue(!BrowserDriver.isElementVisible(webElement2));
    } // test_mouseOverJS_mouseOutJS()

    // QUALENG-97 (mremillard): Testing BrowserDriver methods getBaseURL(), getCurrentURL() and isUrlPresent().
    @When("^I test methods getBaseURL getCurrentURL and isUrlPresent$")
    public void test_getBaseURL_getCurrentURL_isURLPresent() {
        // Note: Both methods getBaseURL() and isURLPresent() call method getCurrentURL().
        // Note: We are also testing the refresh() method here.

        String expectedBaseURL = "http://aso1.mf.qa401.coresys.tmcs";
        assertTrue(expectedBaseURL.equals(BrowserDriver.getBaseURL()));
        BrowserDriver.refresh();
        assertTrue(BrowserDriver.isUrlPresent(expectedBaseURL));
    } // test_getBaseURL_isURLPresent()

    // QUALENG-97 (mremillard): Testing BrowserDriver method getPageSource().
    @When("^I test method getPageSource$")
    public void test_getPageSource() {
        try {
            // Creating a file with the page source and retrieving its path.
            String pageSourcePath = BrowserDriver.getPageSource();

            // Reading in the stored file.
            StringBuffer buffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(pageSourcePath));
            String aLine = br.readLine();
            while (aLine != null) {
                buffer.append(aLine);
                aLine = br.readLine();
            }
            br.close();

            // Checking the page source.
            String pageSource = buffer.toString();
            assertTrue(pageSource.contains("id=\"progressDisplay\""));
        } catch (Exception e) {
            assertTrue(false);
        }
    } // test_getCurrentURL()

    // QUALENG-97 (mremillard): Testing BrowserDriver method switchToFrame()
    @When("^I test method switchToFrame$")
    public void test_switchToFrame() {
        By elementToFind = BrowserDriver.getBy("id=iFrame_input");
        assertTrue(BrowserDriver.switchToFrame((Object) elementToFind, "iFrame"));
    }

    // QUALENG-97 (mremillard): Testing BrowserDriver methods isElementPresent() and isElementNotPresent().
    @When("^I test methods isElementPresent and isElementNotPresent with locators \"(.*?)\" and \"(.*?)\"$")
    public void test_isElementPresent_isElementNotpresent(String pValidLocator, String pInvalidLocator) throws Throwable {
        assertTrue(BrowserDriver.isElementPresent(BrowserDriver.getBy(pValidLocator)));
        assertTrue(!BrowserDriver.isElementPresent(BrowserDriver.getBy(pInvalidLocator)));
        assertTrue(BrowserDriver.isElementNotPresent(BrowserDriver.getBy(pInvalidLocator)));
    } // test_isElementPresent_isElementNotpresent()

    // QUALENG-97 (mremillard): Testing BrowserDriver method waitForElementPresent().
    @When("^I test method waitForElementPresent with locator \"(.*?)\"$")
    public void test_waitForElementPresent(String pLocator) {
        By element = BrowserDriver.getBy(pLocator);
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // I remove the cast (object)
        assertTrue(BrowserDriver.waitForElementPresent(element, 3));
    } // test_waitForElementPresent()

    // QUALENG-97 (mremillard): Testing the browser "dimension" methods.
    @When("^I test the BrowserDriver methods related to the browser dimension$")
    public void test_browserDimensionMethods() {
        // Note: Method setBrowserSize() calls method BrowserDriver.scenarioPrintText().

        Dimension dim = BrowserDriver.getBrowserDimension();
        Dimension dimInner = BrowserDriver.getInnerDimension();

        BrowserDriver.setBrowserSize(dimInner);
        Dimension dimTmp = BrowserDriver.getBrowserDimension();
        assertTrue((dimTmp.height == dimInner.height) && (dimTmp.width == dimInner.width));

        BrowserDriver.setBrowserSize(dim);
        dimTmp = BrowserDriver.getBrowserDimension();
        assertTrue((dimTmp.height == dim.height) && (dimTmp.width == dim.width));
    } // test_browserDimensionMethods()

    // QUALENG-97 (mremillard): Testing the randomName() methods.
    @When("^I test the BrowserDriver randomName methods$")
    public void test_randomName() {
        String name = BrowserDriver.randomName();
        assertTrue(name.startsWith("Test__"));
    } // test_randomName()

    // QUALENG-97 (mremillard): Testing the BrowserDriver method waitForElementNotVisible().
    @When("^I test the BrowserDriver method waitForElementNotVisible with locator \"(.*?)\"$")
    public void test_waitForElementNotVisible(String pLocator) {
        // Testing with a By locator.
        By byElement = BrowserDriver.getBy(pLocator);
        BrowserDriver.waitForElementNotVisible(byElement);

        // Testing with a WebElement.
        // DPA comment this part of test until we catch StaleElementReferenceExecption
        // WebElement webElement = BrowserDriver.getCurrentDriver().findElement(byElement);
        // BrowserDriver.waitForElementNotVisible(webElement, 10);

        // Also testing waitForElementsNotVisible().
        assertTrue(BrowserDriver.waitForElementsNotVisible(byElement));
    } // test_waitForElementNotVisible()

    // QUALENG-97 (mremillard): Testing the BrowserDriver method isImageSrcPresent().
    @When("^I test the BrowserDriver method isImageSrcPresent with locator \"(.*?)\" and value \"(.*?)\"$")
    public void test_isImageSrcPresent(String pLocator, String pSrcValue) {
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.isImageSrcPresent(byElement, pSrcValue));
    } // test_isImageSrcPresent()

    // QUALENG-97 (mremillard): Testing the BrowserDriver method getWindowHandles().
    @When("^I test the BrowserDriver method getWindowHandles$")
    public void test_getWindowHandles() {
        List<String> windowHandles = BrowserDriver.getWindowHandles();
        assertTrue((windowHandles.size() == 1) && (windowHandles.get(0).startsWith("CDwindow")));
    } // test_isImageSrcPresent()

    // QUALENG-97 (mremillard): Testing the BrowserDriver method browserChange().
    @When("^I test the BrowserDriver method browserChange$")
    public void test_browserName() {
        BrowserDriver.browserChange("firefox");
        BrowserDriver.browserChange("chrome");
        WebDriver driver = BrowserDriver.getCurrentDriver();
        assertTrue(driver != null);
    } // test_browserChange()

    // QUALENG-98 (mremillard): Testing the BrowserDriver method getWebElementAttributes().
    @When("^I test the BrowserDriver method getWebElementAttributes with locator \"(.*?)\"$")
    public void test_getWebElementAttributes(String pLocator) {
        Map<String, String> attributes = BrowserDriver.getWebElementAttributes(BrowserDriver.getBy(pLocator));
        assertTrue(attributes.get("id").equals(pLocator.substring(pLocator.indexOf("=") + 1)));
    } // test_getWebElementAttributes()

    // QUALENG-96 (mremillard): Testing the BrowserDriver method waitForAndGetElementWhenClickable().
    @When("^I test the BrowserDriver method waitForAndGetElementWhenClickable with locator \"(.*?)\"$")
    public void test_waitForAndGetElementWhenClickable(String pLocator) {
        By byElement = BrowserDriver.getBy(pLocator);
        assertTrue(BrowserDriver.waitForAndGetElementWhenClickable(byElement, 5) == null);
    } // test_waitForAndGetElementWhenClickable()

    // ****************************************************************************************************
    // The @Then methods.
    // ****************************************************************************************************
    @Then("^the page title start with \"(.*?)\"$")
    public void the_page_title_start_with(String pText) throws Throwable {
        String title = BrowserDriver.getTitle();
        log.info("Validate if title of the page [" + title + "] start with [" + pText + "]");
        assertTrue(title.startsWith(pText));
    }

    @Then("^the page title contains \"(.*?)\"$")
    public void the_page_title_contains(String pText) throws Throwable {
        String title = BrowserDriver.getTitle();
        log.info("Validate if title of the page [" + title + "] start with [" + pText + "]");
        assertTrue(title.contains(pText));
    }

    @Then("^the value of the locator \"([^\"]*)\" is \"(.*?)\"$")
    public void the_value_of_the(String pLocator, String pExpected) throws Throwable {
        String value = BrowserDriver.getAttributeValue(BrowserDriver.getBy(pLocator), "value");
        log.info("Validate the locator [" + pLocator + "]  value [" + value + "] is equal to expected [" + pExpected + "]");
        assertTrue(pExpected.equals(value));
    }

    @Then("^wait a new page load$")
    public void wait_a_new_page_load() throws Throwable {
        log.info("wait a new page load");
        BrowserDriver.waitForPageLoad();
    }

    @Then("^I switch to the last window open$")
    public void i_switch_to_the_last_window_open() {
        List<String> windowHandles = BrowserDriver.getWindowHandles();
        int size = windowHandles.size();
        if (size > 1) {
            // Switching to the last of the windows.
            BrowserDriver.mDriver.switchTo().window(windowHandles.get(size - 1));
            BrowserDriver.wait(1000);
        }
        /**
         * List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles()); int size = windowHandles.size(); if (size > 1) { // Switching to the
         * last of the windows. driver.switchTo().window(windowHandles.get(size - 1)); Utility.pause(1000); }
         * 
         * // Checking that we did indeed make it to the Google web site. if (driver.getTitle().equalsIgnoreCase("Google")) { Log.insert(
         * "The page was brought up successfully.", Log.TYPE_PASS);
         * 
         * WebDriverHelper.closeCurrentWindow(); Utility.pause(1000);
         * 
         * // Switching back to the main window. driver.switchTo().window(currentWindow); if (driver.getTitle().equalsIgnoreCase(testPageTitle)) { Log.insert(
         * "We are back to the main test page.", Log.TYPE_PASS); } else { Log.insert("We did not make it back to the main test page.", Log.TYPE_FAIL);
         * 
         * // Bringing back the main test page. openPage(null); } }
         */
    }

    @Then("^the expected exception occurs$")
    public void the_expected_exception_occurs() throws Exception {
        // Detect if the expected exception is never caught in the test.
        if (expectedException != null) {
            log.error("An expected Exception (" + expectedException.getCanonicalName() + ") never occurs!");
            expectedException = null; // Reset container
            assertTrue(false);
        }
    }

    @Then("^I wait for visible with locator \"(.*?)\"$")
    public void i_wait_for_visible_with_locator(String pLocator) throws Throwable {
        BrowserDriver.waitForElementVisible(BrowserDriver.getBy(pLocator));
    }

    @Then("^I wait for and get WebElement clickable with locator \"(.*?)\"$")
    public void i_wait_for_and_get_webElement_clickable_with_locator(String pLocator) throws Throwable {
        try {
            WebElement we = BrowserDriver.waitForAndGetElementWhenClickable(BrowserDriver.getBy(pLocator), BrowserDriver.getTimeoutInSeconds());
            // validate if the WebElement is not null
            assertTrue(we != null);
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }

    }

    // (mremillard): Validating the selected values of a <select> element.
    @Then("^the selected values of the locator \"([^\"]*)\" are \"(.*?)\"$")
    public void the_selected_values_of_the_locator_are(String pLocator, String pExpectedValues) throws Throwable {
        String[] expectedOptionsTmp = pExpectedValues.split(",");
        List<String> expectedOptions = new ArrayList<String>();
        for (String anOption : expectedOptionsTmp) {
            expectedOptions.add(anOption);
        }

        boolean result = BrowserDriver.validateDropDownSelectedOptions(pLocator, expectedOptions);
        assertTrue(result);
    }

    // (DPA)
    @When("^I navigate to \"([^\"]*)\"$")
    public void i_navaigate_to(String pUrl) throws Throwable {
        log.info("I navigate to [" + pUrl + "].");
        try {
            BrowserDriver.navigateTo(pUrl);
        } catch (Exception e) {
            throwExceptionIfNotExpected(e);
        }
    }

    // ****************************************************************************************************
    // The standard methods.
    // ****************************************************************************************************
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
        if (pException.getClass().equals(expectedException) || pException.getCause().getClass().equals(expectedException)) {
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