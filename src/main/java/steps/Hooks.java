package steps;

import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import listeners.StepEventListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.logging.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class Hooks {
    private static WebDriver webDriver;

    @Before
    public void setup(Scenario scenario) {
        Log.info("----------------- Test scenario [" + scenario.getName() + "] started! -----------------");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        webDriver = new ChromeDriver(options);

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10L));
    }

    @After
    public void teardown(Scenario scenario) {
        logScenario(scenario);
        if (scenario.isFailed()) {
            takeScreenshot();
        }
        webDriver.quit();
    }

    private void logScenario(Scenario scenario) {
        Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
        Log.info("Status: " + scenario.getStatus() + "; Browser: " + capabilities.getBrowserName() + "; Platform: " + capabilities.getPlatformName());
    }

    private void takeScreenshot() {
        TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        String screenshotDestination = "src/main/resources/screenshots/ss_" + new SimpleDateFormat("dd-MM-yyy_HH-mm-ss").format(new Date()) + ".png";
        File destinationFile = new File(screenshotDestination);
        try {
            FileUtils.copyFile(sourceFile, destinationFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @AfterStep
    public void logStep(Scenario scenario) {
        String currentStepName = StepEventListener.getStepName();
        List<String> currentStepArguments = StepEventListener.getStepArguments();
        String currentStepCodeLocation = StepEventListener.getStepCodeLocation();
        if (scenario.getStatus() == Status.FAILED) {
            if (!currentStepArguments.isEmpty()) {
                Log.info("Step failed: " + currentStepName + "; arguments = " + currentStepArguments + "; code_location = " + currentStepCodeLocation);
            } else {
                Log.info("Step failed: " + currentStepName + "; code_location = " + currentStepCodeLocation);
            }
        } else if (scenario.getStatus() == Status.PASSED) {
            Log.info("Step: " + currentStepName);
        } else if (scenario.getStatus() == Status.SKIPPED) {
            Log.info("Step skipped: " + currentStepName);
        }
    }

    public static WebDriver getWebDriver() {
        return webDriver;
    }
}
