package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
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

import java.io.*;
import java.time.Duration;
import java.util.*;

public class StepDefinitions {
    private WebDriver webDriver;
    private Map<String, String> rates = new HashMap<>();

    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        webDriver = new ChromeDriver(options);

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10L));
    }

    @After
    public void close() {
        webDriver.quit();
    }

    @Given("I am on the RSD exchange rate page")
    public void goToRSDPage() {
        webDriver.get("https://www.nbs.rs/sr_RS/finansijsko_trziste/medjubankarsko-devizno-trziste/kursna-lista/zvanicni-srednji-kurs-dinara/index.html");
    }

    @When("I take the average exchange rate for USD, EUR, RUB")
    public void getRates() {
        webDriver.switchTo().frame("frameId");
        List<WebElement> rateTableRows = webDriver.findElements(By.xpath("//table[@id='index:srednjiKursLista']/tbody/tr"));
        rateTableRows.forEach(row -> {
            rates.put(row.findElement(By.cssSelector("td:nth-child(1)")).getText(), row.findElement(By.cssSelector("td:nth-child(5)")).getText());
        });
        System.out.println(rates);
    }

    @Then("I should save it to my spreadsheet")
    public void saveToSheet() throws IOException {
        FileInputStream file = new FileInputStream("C:\\Users\\borda\\Documents\\NBS automatizacija task\\CurrencyTamplate.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        rates.forEach((key, value) -> {
            Cell cell;
            switch (key) {
                case "USD": {
                    cell = sheet.getRow(1).createCell(2);
                    cell.setCellValue(value);
                    break;
                }
                case "EUR": {
                    cell = sheet.getRow(2).createCell(2);
                    cell.setCellValue(value);
                    break;
                }
                case "RUB": {
                    cell = sheet.getRow(3).createCell(2);
                    cell.setCellValue(value);
                    break;
                }
            }
        });
        FileOutputStream out = new FileOutputStream("C:\\Users\\borda\\Documents\\NBS automatizacija task\\CurrencyTamplate.xlsx");
        workbook.write(out);
        out.close();
    }
}
