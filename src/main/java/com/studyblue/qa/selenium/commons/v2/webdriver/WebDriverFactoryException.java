package com.studyblue.qa.selenium.commons.v2.webdriver;


public class WebDriverFactoryException extends RuntimeException {
    public WebDriverFactoryException(String message) {
        super(message);
    }

    public WebDriverFactoryException(String message, Throwable throwable) {
        super(message, throwable);
    }

    private static final long serialVersionUID = 1L;
}
