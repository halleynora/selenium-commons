package com.studyblue.qa.selenium.commons.v2.support;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Canned {@link ExpectedConditionSearchContext}s which are generally useful within SearchContext tests.
 */
public class ExpectedConditionSearchContexts {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExpectedConditionSearchContexts.class);

    private ExpectedConditionSearchContexts() {
        // Utility class
    }

    /**
     * An expectation for checking that an element is present on the DOM of a page. This does not necessarily mean that the element is visible.<br/>
     * Adaptation from {@code ExpectedConditions#presenceOfElementLocated(By)} to use SearchContext
     *
     * @param locator used to find the element
     * @return the WebElement once it is located
     */
    public static ExpectedConditionSearchContext<WebElement> presenceOfElementLocated(final By locator) {
        return new ExpectedConditionSearchContext<WebElement>() {
            @Override
            public WebElement apply(SearchContext driver) {
                return findElement(locator, driver);
            }

            @Override
            public String toString() {
                return "presence of element located by: " + locator;
            }
        };
    }

    /**
     * An expectation for checking that an element is present on the DOM of a page and visible. Visibility means that the element is not only displayed but also
     * has a height and width that is greater than 0.<br/>
     * Adaptation from {@code ExpectedConditions#visibilityOfElementLocated(By)} to use SearchContext
     *
     * @param locator used to find the element
     * @return the WebElement once it is located and visible
     */
    public static ExpectedConditionSearchContext<WebElement> visibilityOfElementLocated(final By locator) {
        return new ExpectedConditionSearchContext<WebElement>() {
            @Override
            public WebElement apply(SearchContext driver) {
                try {
                    return elementIfVisible(findElement(locator, driver));
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "visibility of element located by " + locator;
            }
        };
    }

    /**
     * An expectation for checking that all elements present on the web page that match the locator are visible. Visibility means that the elements are not only
     * displayed but also have a height and width that is greater than 0.<br/>
     * Adaptation from {@code ExpectedConditions#visibilityOfAllElementsLocatedBy(By)} to use SearchContext
     *
     * @param locator used to find the element
     * @return the list of WebElements once they are located
     */
    public static ExpectedConditionSearchContext<List<WebElement>> visibilityOfAllElementsLocatedBy(final By locator) {
        return new ExpectedConditionSearchContext<List<WebElement>>() {
            @Override
            public List<WebElement> apply(SearchContext driver) {
                List<WebElement> elements = findElements(locator, driver);
                for (WebElement element : elements) {
                    if (!element.isDisplayed()) {
                        return null;
                    }
                }
                return elements.size() > 0 ? elements : null;
            }

            @Override
            public String toString() {
                return "visibility of all elements located by " + locator;
            }
        };
    }

    /**
     * An expectation for checking that all elements present on the web page that match the locator are visible. Visibility means that the elements are not only
     * displayed but also have a height and width that is greater than 0.<br/>
     * Adaptation from {@code ExpectedConditions#visibilityOfAllElements(By)} to use SearchContext
     *
     * @param elements list of WebElements
     * @return the list of WebElements once they are located
     */
    public static ExpectedConditionSearchContext<List<WebElement>> visibilityOfAllElements(final List<WebElement> elements) {
        return new ExpectedConditionSearchContext<List<WebElement>>() {
            @Override
            public List<WebElement> apply(SearchContext driver) {
                for (WebElement element : elements) {
                    if (!element.isDisplayed()) {
                        return null;
                    }
                }
                return elements.size() > 0 ? elements : null;
            }

            @Override
            public String toString() {
                return "visibility of all " + elements;
            }
        };
    }

    /**
     * An expectation for checking that an element, known to be present on the DOM of a page, is visible. Visibility means that the element is not only
     * displayed but also has a height and width that is greater than 0. <br/>
     * Adaptation from {@code ExpectedConditions#visibilityOf(WebElement))} to use SearchContext
     *
     * @param element the WebElement
     * @return the (same) WebElement once it is visible
     */
    public static ExpectedConditionSearchContext<WebElement> visibilityOf(final WebElement element) {
        return new ExpectedConditionSearchContext<WebElement>() {
            @Override
            public WebElement apply(SearchContext driver) {
                return elementIfVisible(element);
            }

            @Override
            public String toString() {
                return "visibility of " + element;
            }
        };
    }

    /**
     * @return the given element if it is visible and has non-zero size, otherwise null.
     */
    private static WebElement elementIfVisible(WebElement element) {
        return element.isDisplayed() ? element : null;
    }

    /**
     * An expectation for checking that there is at least one element present on a web page.<br/>
     * Adaptation from {@code ExpectedConditions#presenceOfAllElementsLocatedBy(By)} to use SearchContext
     *
     * @param locator used to find the element
     * @return the list of WebElements once they are located
     */
    public static ExpectedConditionSearchContext<List<WebElement>> presenceOfAllElementsLocatedBy(final By locator) {
        return new ExpectedConditionSearchContext<List<WebElement>>() {
            @Override
            public List<WebElement> apply(SearchContext driver) {
                List<WebElement> elements = findElements(locator, driver);
                return elements.size() > 0 ? elements : null;
            }

            @Override
            public String toString() {
                return "presence of any elements located by " + locator;
            }
        };
    }

    /**
     * An expectation for checking if the given text is present in the specified element.<br/>
     * Adaptation from {@code ExpectedConditions#textToBePresentInElement(WebElement, String)} to use SearchContext
     *
     * @param element the WebElement
     * @param text to be present in the element
     * @return true once the element contains the given text
     */
    public static ExpectedConditionSearchContext<Boolean> textToBePresentInElement(final WebElement element, final String text) {

        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    String elementText = element.getText();
                    return elementText.contains(text);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be present in element %s", text, element);
            }
        };
    }

    /**
     * An expectation for checking if the given text is present in the element that matches the given locator.<br/>
     * Adaptation from {@code ExpectedConditions#textToBePresentInElementLocated(By, String)} to use SearchContext
     *
     * @param locator used to find the element
     * @param text to be present in the element found by the locator
     * @return true once the first element located by locator contains the given text
     */
    public static ExpectedConditionSearchContext<Boolean> textToBePresentInElementLocated(final By locator, final String text) {

        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    String elementText = findElement(locator, driver).getText();
                    return elementText.contains(text);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be present in element found by %s", text, locator);
            }
        };
    }

    /**
     * An expectation for checking if the given text is present in the specified elements value attribute. <br/>
     * Adaptation from {@code ExpectedConditions#textToBePresentInElementValue(By, String)} to use SearchContext
     *
     * @param element the WebElement
     * @param text to be present in the element's value attribute
     * @return true once the element's value attribute contains the given text
     */
    public static ExpectedConditionSearchContext<Boolean> textToBePresentInElementValue(final WebElement element, final String text) {

        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    String elementText = element.getAttribute("value");
                    if (elementText != null) {
                        return elementText.contains(text);
                    } else {
                        return false;
                    }
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be the value of element %s", text, element);
            }
        };
    }

    /**
     * An expectation for checking if the given text is present in the specified elements value attribute.<br/>
     * Adaptation from {@code ExpectedConditions#textToBePresentInElementValue(By, String)} to use SearchContext
     *
     * @param locator used to find the element
     * @param text to be present in the value attribute of the element found by the locator
     * @return true once the value attribute of the first element located by locator contains the given text
     */
    public static ExpectedConditionSearchContext<Boolean> textToBePresentInElementValue(final By locator, final String text) {

        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    String elementText = findElement(locator, driver).getAttribute("value");
                    if (elementText != null) {
                        return elementText.contains(text);
                    } else {
                        return false;
                    }
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be the value of element located by %s", text, locator);
            }
        };
    }

    /**
     * An expectation for checking that an element is either invisible or not present on the DOM. <br/>
     * Adaptation from {@code ExpectedConditions#invisibilityOfElementLocated(By)} to use SearchContext
     *
     * @param locator used to find the element
     */
    public static ExpectedConditionSearchContext<Boolean> invisibilityOfElementLocated(final By locator) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    return !(findElement(locator, driver).isDisplayed());
                } catch (NoSuchElementException e) {
                    // Returns true because the element is not present in DOM. The
                    // try block checks if the element is present but is invisible.
                    return true;
                } catch (StaleElementReferenceException e) {
                    // Returns true because stale element reference implies that element
                    // is no longer visible.
                    return true;
                }
            }

            @Override
            public String toString() {
                return "element to no longer be visible: " + locator;
            }
        };
    }

    /**
     * An expectation for checking that an element with text is either invisible or not present on the DOM.<br/>
     * Adaptation from {@code ExpectedConditions#invisibilityOfElementWithText(By, String)} to use SearchContext
     *
     * @param locator used to find the element
     * @param text of the element
     */
    public static ExpectedConditionSearchContext<Boolean> invisibilityOfElementWithText(final By locator, final String text) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    return !findElement(locator, driver).getText().equals(text);
                } catch (NoSuchElementException e) {
                    // Returns true because the element with text is not present in DOM. The
                    // try block checks if the element is present but is invisible.
                    return true;
                } catch (StaleElementReferenceException e) {
                    // Returns true because stale element reference implies that element
                    // is no longer visible.
                    return true;
                }
            }

            @Override
            public String toString() {
                return String.format("element containing '%s' to no longer be visible: %s", text, locator);
            }
        };
    }

    /**
     * An expectation for checking an element is visible and enabled such that you can click it. <br/>
     * Adaptation from {@code ExpectedConditions#elementToBeClickable(By)} to use SearchContext
     *
     * @param locator used to find the element
     * @return the WebElement once it is located and clickable (visible and enabled)
     */
    public static ExpectedConditionSearchContext<WebElement> elementToBeClickable(final By locator) {
        return new ExpectedConditionSearchContext<WebElement>() {

            public ExpectedConditionSearchContext<WebElement> visibilityOfElementLocated = ExpectedConditionSearchContexts.visibilityOfElementLocated(locator);

            @Override
            public WebElement apply(SearchContext driver) {
                WebElement element = visibilityOfElementLocated.apply(driver);
                try {
                    if (element != null && element.isEnabled()) {
                        return element;
                    } else {
                        return null;
                    }
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "element to be clickable: " + locator;
            }
        };
    }

    /**
     * An expectation for checking an element is visible and enabled such that you can click it. <br/>
     * Adaptation from {@code ExpectedConditions#elementToBeClickable(WebElement)} to use SearchContext
     *
     * @param element the WebElement
     * @return the (same) WebElement once it is clickable (visible and enabled)
     */
    public static ExpectedConditionSearchContext<WebElement> elementToBeClickable(final WebElement element) {
        return new ExpectedConditionSearchContext<WebElement>() {

            public ExpectedConditionSearchContext<WebElement> visibilityOfElement = ExpectedConditionSearchContexts.visibilityOf(element);

            @Override
            public WebElement apply(SearchContext driver) {
                WebElement element = visibilityOfElement.apply(driver);
                try {
                    if (element != null && element.isEnabled()) {
                        return element;
                    } else {
                        return null;
                    }
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "element to be clickable: " + element;
            }
        };
    }

    /**
     * Wait until an element is no longer attached to the DOM. <br/>
     * Adaptation from {@code ExpectedConditions#stalenessOf(WebElement)} to use SearchContext
     *
     * @param element The element to wait for.
     * @return false is the element is still attached to the DOM, true otherwise.
     */
    public static ExpectedConditionSearchContext<Boolean> stalenessOf(final WebElement element) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext ignored) {
                try {
                    // Calling any method forces a staleness check
                    element.isEnabled();
                    return false;
                } catch (StaleElementReferenceException expected) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return String.format("element (%s) to become stale", element);
            }
        };
    }

    /**
     * Wrapper for a condition, which allows for elements to update by redrawing.<br/>
     *
     * This works around the problem of conditions which have two parts: find an element and then check for some condition on it. For these conditions it is
     * possible that an element is located and then subsequently it is redrawn on the client. When this happens a {@link StaleElementReferenceException} is
     * thrown when the second part of the condition is checked.<br/>
     * Adaptation from {@code ExpectedConditions#refreshed(org.openqa.selenium.support.ui.ExpectedCondition)} to use SearchContext
     */
    public static <T> ExpectedConditionSearchContext<T> refreshed(final ExpectedConditionSearchContext<T> condition) {
        return new ExpectedConditionSearchContext<T>() {
            @Override
            public T apply(SearchContext driver) {
                try {
                    return condition.apply(driver);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("condition (%s) to be refreshed", condition);
            }
        };
    }

    /**
     * An expectation for checking if the given element is selected.<br/>
     * Adaptation from {@code ExpectedConditions#elementToBeSelected(WebElement)} to use SearchContext
     */
    public static ExpectedConditionSearchContext<Boolean> elementToBeSelected(final WebElement element) {
        return elementSelectionStateToBe(element, true);
    }

    /**
     * An expectation for checking if the given element is selected.<br/>
     * Adaptation from {@code ExpectedConditions#elementSelectionStateToBe(WebElement, boolean)} to use SearchContext
     */
    public static ExpectedConditionSearchContext<Boolean> elementSelectionStateToBe(final WebElement element, final boolean selected) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                return element.isSelected() == selected;
            }

            @Override
            public String toString() {
                return String.format("element (%s) to %sbe selected", element, (selected ? "" : "not "));
            }
        };
    }

    public static ExpectedConditionSearchContext<Boolean> elementToBeSelected(final By locator) {
        return elementSelectionStateToBe(locator, true);
    }

    public static ExpectedConditionSearchContext<Boolean> elementSelectionStateToBe(final By locator, final boolean selected) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    return element.isSelected() == selected;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("element found by %s to %sbe selected", locator, (selected ? "" : "not "));
            }
        };
    }

    /**
     * An expectation with the logical opposite condition of the given condition.<br/>
     * Adaptation from {@code ExpectedConditions#not(org.openqa.selenium.support.ui.ExpectedCondition))} to use SearchContext
     * 
     */
    public static ExpectedConditionSearchContext<Boolean> not(final ExpectedConditionSearchContext<?> condition) {
        return new ExpectedConditionSearchContext<Boolean>() {
            @Override
            public Boolean apply(SearchContext driver) {
                Object result = condition.apply(driver);
                return result == null || result == Boolean.FALSE;
            }

            @Override
            public String toString() {
                return "condition to not be valid: " + condition;
            }
        };
    }

    /**
     * Looks up an element. Logs and re-throws WebDriverException if thrown.
     * <p/>
     * Method exists to gather data for http://code.google.com/p/selenium/issues/detail?id=1800
     */
    private static WebElement findElement(By by, SearchContext driver) {
        try {
            return driver.findElement(by);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (WebDriverException e) {
            LOGGER.warn("WebDriverException thrown by findElement({})", by, e);
            throw e;
        }
    }

    /**
     * @see #findElement(By, WebDriver)
     */
    private static List<WebElement> findElements(By by, SearchContext driver) {
        try {
            return driver.findElements(by);
        } catch (WebDriverException e) {
            LOGGER.warn("WebDriverException thrown by findElement({})", by, e);
            throw e;
        }
    }
}
