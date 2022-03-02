package utils.selectors.xpath;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import steps.Hooks;

import java.util.List;

public class XPathHelper {
    private final WebDriver webDriver = Hooks.getWebDriver();
    private String path = "/";

    public XPathHelper find(String element) {
        this.path += String.format("/%s", element);
        return this;
    }

    public XPathHelper find(String element, String attribute, String attributeValue) {
        this.path += String.format("/%s[@%s='%s']", element, attribute, attributeValue);
        return this;
    }

    public XPathHelper findWithFunction(String element, String functionName, String... functionArguments) {
        this.path += buildPathWithFunction(element, functionName, functionArguments);
        return this;
    }

    private String buildPathWithFunction(String element, String functionName, String... functionArguments) {
        String xpath = String.format("/%s[%s(", element, functionName);
        for (String argument : functionArguments) {
            xpath = xpath.concat(argument + ",");
        }
        xpath = xpath.substring(0, xpath.length()-1);
        xpath = xpath.concat(")]");
        return xpath;
    }

    public WebElement buildElement() {
        WebElement element = webDriver.findElement(By.xpath(this.path));
        this.path = "/";
        return element;
    }

    public List<WebElement> buildElements() {
        List<WebElement> elements = webDriver.findElements(By.xpath(this.path));
        this.path = "/";
        return elements;
    }
}
