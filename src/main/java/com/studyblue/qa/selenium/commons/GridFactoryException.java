package com.studyblue.qa.selenium.commons;


public class GridFactoryException extends Exception {
    public GridFactoryException(String pMessage) {
        super(pMessage);
    }

    // Messages:
    // Error forwarding the new session Empty pool of VM for setup Capabilities
    // ...
    public GridFactoryException(String pMessage, Throwable pThrow) {
        super(pMessage, pThrow);
    }

    private static final long serialVersionUID = 1L;
}
