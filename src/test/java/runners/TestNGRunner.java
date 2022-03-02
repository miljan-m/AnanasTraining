package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions
        (features = "src/test/resources/features/",
        tags = "not @AverageRate",
        glue = {"steps"})

public class TestNGRunner extends AbstractTestNGCucumberTests {

}
