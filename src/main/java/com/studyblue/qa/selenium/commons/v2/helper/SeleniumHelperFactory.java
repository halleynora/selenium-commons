package com.studyblue.qa.selenium.commons.v2.helper;

import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioGridInfoPrinter;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioHelper;
import com.studyblue.qa.selenium.commons.v2.helper.scenario.ScenarioScreenShot;

public interface SeleniumHelperFactory {

    ActionHelper getActionHelper();

    NavigatorHelper getNavigatorHelper();

    ScenarioGridInfoPrinter getScenarioGridInfoPrinter();

    ScenarioScreenShot getScenarioScreenShot();

    ScenarioHelper getScenarioHelper();

    WaitHelper getWaitHelper();

    VisibilityHelper getVisibilityHelper();

}