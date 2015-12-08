package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.support.ui.WebDriverWait;

public interface WebDriverWaitFactory {

    WebDriverWait createWebDriverWait(long timeOutInSeconds);

    WebDriverWait createWebDriverWait();

}
