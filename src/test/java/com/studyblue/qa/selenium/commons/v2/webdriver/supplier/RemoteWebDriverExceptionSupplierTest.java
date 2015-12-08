package com.studyblue.qa.selenium.commons.v2.webdriver.supplier;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.studyblue.qa.selenium.commons.v2.webdriver.WebDriverFactoryException;

public class RemoteWebDriverExceptionSupplierTest {

    @Test
    public void ShouldThrowExceptionWithHubUrlInMessage() throws MalformedURLException {
        URL url1 = new URL("http://google.com/");
        URL url2 = new URL("http://yahoo.com");

        try {
            RemoteWebDriverExceptionSupplier supplier = new RemoteWebDriverExceptionSupplier(Lists.newArrayList(url1, url2));
            supplier.get();
        } catch (WebDriverFactoryException e) {
            Assert.assertTrue(e.getMessage().contains(url1.toString()));
            Assert.assertTrue(e.getMessage().contains(url2.toString()));
        }
    }

    @Test(expected = WebDriverFactoryException.class)
    public void ShouldThrowWebDriverFactoryException() throws MalformedURLException {
        RemoteWebDriverExceptionSupplier supplier = new RemoteWebDriverExceptionSupplier(Lists.newArrayList(new URL("http://www.google.com")));
        supplier.get();
        Assert.fail();
    }

}
