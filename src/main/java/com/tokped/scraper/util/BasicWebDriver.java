package com.tokped.scraper.util;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasicWebDriver {
    private static final String USER_AGENT =
            "user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36";
    private static final String GOOGLE_URL = "https://www.google.com";

    private static final String JS_WINDOW_OPEN = "window.open()";
    private static final String JS_SCROLL_SMALL = "window.scrollBy(0,300)";
    private static final String JS_SCROLL_MEDIUM = "window.scrollBy(0,600)";
    private static final String JS_REMOVE_ELEMENT = "document.querySelector('%s').parentElement.remove();";

    private static final String EMPTY = "";

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor jsExecutor;

    public BasicWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments(USER_AGENT);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 5);
        jsExecutor = (JavascriptExecutor) driver;
    }

    public List<String> prepareTwoTabs() {
        driver.get(GOOGLE_URL);
        jsExecutor.executeScript(JS_WINDOW_OPEN);
        return new ArrayList<> (driver.getWindowHandles());
    }

    public List<WebElement> getElementListByScrollingDown(String url, String xpath, String tab) {
        switchTab(tab);
        driver.get(url);
        jsExecutor.executeScript(JS_SCROLL_MEDIUM);
        return driver.findElements(By.xpath(xpath));
    }

    public void getWebpage(String path, String tab) {
        switchTab(tab);
        driver.get(path);
    }

    public void waitOnElement(String xpath) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(xpath)));
    }

    public void removeElement(String element) {
        jsExecutor.executeScript(String.format(JS_REMOVE_ELEMENT, element));
    }

    public void scrollDownSmall() {
        jsExecutor.executeScript(JS_SCROLL_SMALL);
    }

    public String getText(String xpath) {
        return driver.findElements(By.xpath(xpath)).isEmpty()
                ? EMPTY : driver.findElement(By.xpath(xpath)).getText();
    }

    public String getText(String xpath, String attribute) {
        return driver.findElements(By.xpath(xpath)).isEmpty()
                ? EMPTY : driver.findElement(By.xpath(xpath)).getAttribute(attribute);
    }

    public void switchTab(String tab) {
        driver.switchTo().window(tab);
    }

    public void quit() {
        driver.quit();
    }
}