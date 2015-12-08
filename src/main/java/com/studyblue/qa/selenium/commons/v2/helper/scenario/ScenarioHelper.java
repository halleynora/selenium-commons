package com.studyblue.qa.selenium.commons.v2.helper.scenario;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class ScenarioHelper {

    private final Supplier<Scenario> scenarioSuplier;

    public ScenarioHelper(Supplier<Scenario> scenarioSuplier) {
        this.scenarioSuplier = checkNotNull(scenarioSuplier);
    }

    public Scenario getScenario() {
        return scenarioSuplier.get();
    }

    public void printToggleText(String title, String text) {
    
        String unicId = UUID.randomUUID().toString();
        String html = "<a onclick=\"toggleDiv=document.getElementById('" + unicId
                + "'); toggleDiv.style.display = (toggleDiv.style.display == 'none' ? 'block' : 'none');return false\" href=\"\">" + title + "</a>"
                + "<div id=\"" + unicId + "\" style=\"display:none ; white-space: pre-wrap; word-break:break-all'\">" + text + "</div><br>";
        getScenario().write(html);
    }
    
    public void printText(String text) {
        getScenario().write(text + "<br>");
    }

}