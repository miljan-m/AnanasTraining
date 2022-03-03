package utils.selectors.css;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import steps.Hooks;

import java.util.List;

public class CSSHelper {
    private final WebDriver webDriver = Hooks.getWebDriver();
    private String path = "";
    private WebElement parent = null;

    public CSSHelper withParent(WebElement parent) {
        this.parent = parent;
        return this;
    }

    public CSSHelper element(String name) {
        this.path += name;
        return this;
    }

    public CSSHelper combinator(String symbol) {
        if (!symbol.equals(" ") && !symbol.equals(">") && !symbol.equals("~") && !symbol.equals("+") && !symbol.equals("||")) {
            throw new RuntimeException("Invalid combinator.");
        }
        this.path += symbol;
        return this;
    }

    public CSSHelper pseudoClass(String pseudo) {
        this.path += ":" + pseudo;
        return this;
    }

    public CSSHelper pseudoElement(String pseudo) {
        this.path += "::" + pseudo;
        return this;
    }

    public WebElement buildElement() {
        if (this.parent != null) {
            WebElement element = this.parent.findElement(By.cssSelector(this.path));
            this.parent = null;
            this.path = "";
            return element;
        }
        WebElement element = webDriver.findElement(By.cssSelector(this.path));
        this.path = "";
        return element;
    }

    public List<WebElement> buildElements() {
        if (this.parent != null) {
            List<WebElement> elements = webDriver.findElements(By.cssSelector(this.path));
            this.parent = null;
            this.path = "";
            return elements;
        }
        List<WebElement> elements = webDriver.findElements(By.cssSelector(this.path));
        this.path = "";
        return elements;
    }
}
