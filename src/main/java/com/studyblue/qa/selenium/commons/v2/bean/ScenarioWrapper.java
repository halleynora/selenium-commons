package com.studyblue.qa.selenium.commons.v2.bean;

import java.util.Collection;

import com.google.common.base.Supplier;

import cucumber.api.Scenario;

public class ScenarioWrapper implements Scenario {
    
    private final Supplier<Scenario> scenarioSupplier;

    public ScenarioWrapper(Supplier<Scenario> scenarioSupplier) {
        super();
        this.scenarioSupplier = scenarioSupplier;
    }

    @Override
    public Collection<String> getSourceTagNames() {
        return scenarioSupplier.get().getSourceTagNames();
    }

    @Override
    public String getStatus() {
        return scenarioSupplier.get().getStatus();
    }

    @Override
    public boolean isFailed() {
        return scenarioSupplier.get().isFailed();
    }

    @Override
    public void embed(byte[] data, String mimeType) {
        scenarioSupplier.get().embed(data, mimeType);
    }

    @Override
    public void write(String text) {
        scenarioSupplier.get().write(text);
    }

    @Override
    public String getName() {
        return scenarioSupplier.get().getName();
    }

}
