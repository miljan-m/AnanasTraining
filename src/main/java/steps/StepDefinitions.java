package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import utils.ExcelUtility;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class StepDefinitions {
    private final WebDriver webDriver = Hooks.getWebDriver();
    private final Map<String, String> rates = new HashMap<>();

    @Given("I am on page {string}")
    public void goToPage(String URL) {
        webDriver.get(URL);
    }

    @When("I take the exchange rate for all currencies from table {string}")
    public void getRates(String tableName) {
        webDriver.switchTo().frame("frameId");

        List<WebElement> rateTableRows = webDriver.findElements(By.xpath("//table[@id='index:" + tableName + "']/tbody/tr"));
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
        webDriver.findElement(By.xpath("//a[contains(text(),'Курсна листа НБС')]")).click();
        webDriver.findElement(By.xpath("//a[contains(text(),'На жељени дан')]")).click();
    }

    @And("I input the previous work day and show the list")
    public void iInputThePreviousWorkDayAndShowTheList() {
        webDriver.switchTo().frame("frameId");
        webDriver.findElement(By.xpath("//input[@id='index:inputCalendar1']")).click();

        List<WebElement> daysBeforeToday = webDriver.findElements(By.xpath("//div[@class='dhtmlxcalendar_dates_cont']/ul/li"))
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

        webDriver.findElement(By.xpath("//button[@id='index:buttonShow']")).click();
        webDriver.switchTo().defaultContent();
    }
}
