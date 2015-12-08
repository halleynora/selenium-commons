package com.studyblue.qa.selenium.commons.stepdef;

import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.runtime.java.StepDefAnnotation;

public class BrowserLogPrinter implements InitializingBean {
    
    @Value("${BrowserConsoleDumpScenarioHook.browserLogLevel:SEVERE}")
    private String browserLogLevel;

    private Level translatedLogLevel;
    
    @Autowired
    private WebDriver webDriver;
    
    @After
    public void printBrowserLogs(Scenario scenario) {
        try {
            scenario.write("Dumping browser console output");
            for (LogEntry entry : getBrowserLogs()) {
                printLogEntry(scenario, entry);
            }
        } catch (Exception e) {
            // no-op
        }
    }
    
    private LogEntries getBrowserLogs() {
        return webDriver.manage().logs().get(LogType.BROWSER);
    }
    
    private void printLogEntry(Scenario scenario, LogEntry entry) {
        if (isLevelEnabled(entry.getLevel())) {
            scenario.write(formatLogEntry(entry));
        }
    }

    private boolean isLevelEnabled(Level level) {
        return translatedLogLevel != null ? level.intValue() >= translatedLogLevel.intValue() : false;
    }
    
    private String formatLogEntry(LogEntry entry) {
        return String.format("[%s] %s", entry.getLevel().toString(), entry.getMessage());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            translatedLogLevel = Level.parse(browserLogLevel.trim());
        } catch (Exception e) {
            // Well, guess you didn't want to log anyway...
        }
        
    }

}
