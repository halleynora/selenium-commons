package com.studyblue.qa.selenium.commons.v2;

public interface Browser {
    
    String FIREFOX = "firefox";
    String CHROME = "chrome";
    String PHANTOMJS = "phantomjs";
    String INTERNET_EXPLORER = "internetexplorer";
    String SAFARI = "safari";

    String getName();

    public static enum BrowserEnum implements Browser {
        firefox(FIREFOX), chrome(CHROME), phantomjs(PHANTOMJS), internetexplorer(INTERNET_EXPLORER), safari(SAFARI);

        public final String name;

        private BrowserEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}