package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.selectors.xpath.XPathHelper;
import utils.excel.ExcelUtility;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StepDefinitions {
    private final WebDriver webDriver = Hooks.getWebDriver();
    private final Map<String, String> rates = new HashMap<>();
    private final XPathHelper xPathHelper = new XPathHelper();

    @Given("I am on page {string}")
    public void goToPage(String URL) {
        webDriver.get(URL);
    }

    @When("I take the exchange rates for all currencies from table {string}")
    public void getRates(String tableName) {
        webDriver.switchTo().frame("frameId");

        //table[@id='index:" + tableName + "']/tbody/tr
        List<WebElement> rateTableRows = xPathHelper.find("table", "id", "index:" + tableName).find("tbody").find("tr").buildElements();
        rateTableRows.forEach(row -> {
            String currencySymbol = row.findElement(By.cssSelector("td:nth-child(1)")).getText();
            String exchangeRate = row.findElement(By.cssSelector("td:nth-child(5)")).getText();
            rates.put(currencySymbol, exchangeRate);
        });
        System.out.println(rates);

        webDriver.switchTo().defaultContent();
    }

    @Then("I save the results based on template {string}")
    public void saveToSheet(String template) throws IOException {
        ExcelUtility.saveResultsBasedOnTemplate(rates, "src/main/resources/templates/" + template);
    }

    @When("I navigate to the exchange rates by day filter page")
    public void iNavigateToExchangeRatesByDayFilterPage() {
        xPathHelper.findWithFunction("a", "contains", "text()", "'Курсна листа НБС'").buildElement().click();
        xPathHelper.findWithFunction("a", "contains", "text()", "'На жељени дан'").buildElement().click();
    }

    @And("I input the previous work day and show the list")
    public void iInputThePreviousWorkDayAndShowTheList() {
        webDriver.switchTo().frame("frameId");

        xPathHelper.find("input", "id", "index:inputCalendar1").buildElement().click();

        List<WebElement> daysBeforeToday = xPathHelper.find("div", "class", "dhtmlxcalendar_dates_cont").find("ul").find("li").buildElements()
                .stream().takeWhile(day -> !day.getAttribute("class").endsWith("date"))
                .collect(Collectors.toList());

        WebElement lastWorkDay;
        for (int i = daysBeforeToday.size()-1; i >= 0; i--) {
            if (!daysBeforeToday.get(i).getAttribute("class").endsWith("weekend")) {
                lastWorkDay = daysBeforeToday.get(i);
                lastWorkDay.click();
                break;
            }
        }

        xPathHelper.find("button", "id", "index:buttonShow").buildElement().click();
        webDriver.switchTo().defaultContent();
    }
}
