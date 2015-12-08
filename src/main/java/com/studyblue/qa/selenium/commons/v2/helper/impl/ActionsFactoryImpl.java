package com.studyblue.qa.selenium.commons.v2.helper.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.studyblue.qa.selenium.commons.v2.helper.ActionsFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;

public class ActionsFactoryImpl implements ActionsFactory {

    private final HasInputDevices hasInputDevices;

    public ActionsFactoryImpl(HasInputDevices hasInputDevices) {
        this.hasInputDevices = checkNotNull(hasInputDevices);
    }

    public ActionsFactoryImpl(WebDriver webDriver) {
        this(toHasInputDevices(webDriver));
    }

    private static HasInputDevices toHasInputDevices(WebDriver webDriver) {
        if (webDriver instanceof HasInputDevices) {
            return (HasInputDevices) webDriver;
        }
        throw new RuntimeException("webDriver do not implement HasInputDevices");
    }

    @Override
    public Actions createActions() {
        return new Actions(hasInputDevices.getKeyboard(), hasInputDevices.getMouse());
    }

}
