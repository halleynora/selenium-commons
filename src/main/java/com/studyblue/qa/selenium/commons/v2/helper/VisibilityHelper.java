package com.studyblue.qa.selenium.commons.v2.helper;

import org.openqa.selenium.By;

public interface VisibilityHelper {

    public void failIfElementNotVisible(By by);

    public void failIfElementNotVisible(By by, String message);

    public boolean isElementVisible(By by);

    public boolean isElementNotVisible(By by);

    public void failIfElementVisible(By by);

    public void failIfElementVisible(By by, String message);

}
