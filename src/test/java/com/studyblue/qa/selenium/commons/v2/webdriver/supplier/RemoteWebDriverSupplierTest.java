package com.studyblue.qa.selenium.commons.v2.webdriver.supplier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Supplier;
import com.studyblue.qa.selenium.commons.v2.webdriver.WebDriverFactoryException;

public class RemoteWebDriverSupplierTest {

    @Test(expected = NullPointerException.class)
    public void constructorShouldTrowOnNullURl() {
        new RemoteWebDriverSupplier(mock(DesiredCapabilities.class), null, mock(Supplier.class));
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldTrowOnNullDesiredCapabilities() throws MalformedURLException {

        new RemoteWebDriverSupplier(null, new URL("http://www.google.com"), mock(Supplier.class));
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldTrowOnNullSupplier() throws MalformedURLException {
        new RemoteWebDriverSupplier(mock(DesiredCapabilities.class), new URL("http://www.google.com"), null);
    }

    @Test
    public void shouldCallFallbackSuplierOnException() throws MalformedURLException {

        MockSupplier fallbacSupplier = new MockSupplier();

        RemoteWebDriverSupplier supplier = new RemoteWebDriverSupplier(mock(DesiredCapabilities.class), new URL("http://www.google.com"),
                fallbacSupplier) {

            @Override
            protected RemoteWebDriver createWebDriver(URL hubUrl, DesiredCapabilities capability) {
                throw new RuntimeException();
            }

        };

        assertNotNull(supplier.get());
        assertTrue(fallbacSupplier.called);
    }

    @Test(expected = WebDriverFactoryException.class)
    public void shouldNotCAtchFallbackSuplierException() throws MalformedURLException {

        Supplier<WebDriver> fallbacSupplier = new Supplier<WebDriver>() {

            @Override
            public WebDriver get() {
                throw new WebDriverFactoryException("");
            }

        };

        RemoteWebDriverSupplier supplier = new RemoteWebDriverSupplier(mock(DesiredCapabilities.class), new URL("http://www.google.com"),
                fallbacSupplier) {

            @Override
            protected RemoteWebDriver createWebDriver(URL hubUrl, DesiredCapabilities capability) {
                throw new RuntimeException();
            }

        };

        supplier.get();
    }

    private class MockSupplier implements Supplier<WebDriver> {

        boolean called;

        @Override
        public WebDriver get() {
            called = true;
            return mock(RemoteWebDriver.class);
        }

    }

}
