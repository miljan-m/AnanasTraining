package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jdbc.Statements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.logging.Log;
import utils.selectors.CSSHelper;
import utils.selectors.XPathHelper;
import utils.excel.ExcelUtility;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StepDefinitions {
    private final WebDriver webDriver = Hooks.getWebDriver();
    private final Map<String, String> rates = new HashMap<>();
    private final XPathHelper xPathHelper = new XPathHelper();
    private final CSSHelper cssHelper = new CSSHelper();

    @Given("I am on page {string}")
    public void goToPage(String URL) {
        webDriver.get(URL);
        Log.info("I went to " + URL + " !!!");
    }

    @When("I take the average exchange rates for all currencies from table {string}")
    public void getAverageRates(String tableName) {
        webDriver.switchTo().frame("frameId");

        //table[@id='index:srednjiKurs']/tbody/tr
        List<WebElement> rateTableRows = xPathHelper.find("table", "id", tableName).find("tbody").find("tr").buildElements();
        rateTableRows.forEach(row -> {
            String currencySymbol = cssHelper.withParent(row).element("td").pseudoClass("nth-child(1)").buildElement().getText();
            String exchangeRate = cssHelper.withParent(row).element("td").pseudoClass("nth-child(5)").buildElement().getText();
            rates.put(currencySymbol, exchangeRate);
        });
        System.out.println(rates);

        webDriver.switchTo().defaultContent();
    }

    @When("I take the buying and selling exchange rates for all currencies from table {string}")
    public void getBuyingAndSellingRates(String tableName) {
        webDriver.switchTo().frame("frameId");

        List<WebElement> rateTableRows = xPathHelper.find("table", "id", tableName).find("tbody").find("tr").buildElements();
        rateTableRows.forEach(row -> {
            String currencySymbol = cssHelper.withParent(row).element("td").pseudoClass("nth-child(1)").buildElement().getText();
            String buyingRate = cssHelper.withParent(row).element("td").pseudoClass("nth-child(5)").buildElement().getText();
            String sellingRate = cssHelper.withParent(row).element("td").pseudoClass("nth-child(6)").buildElement().getText();
            rates.put(currencySymbol, buyingRate+";"+sellingRate);
        });
        System.out.println(rates);

        webDriver.switchTo().defaultContent();
    }

    @Then("I save the results based on template {string}")
    public void saveResults(String template) throws IOException {
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

    @And("I save the average rates to a database")
    public void iSaveTheAverageRatesToADatabase() {
        Statements.insertAverageRates(rates);
    }

    @And("I save the buy and sell rates to a database")
    public void iSaveTheBuyAndSellRatesToADatabase() {
        Statements.insertBuyAndSellRates(rates);
    }
}
