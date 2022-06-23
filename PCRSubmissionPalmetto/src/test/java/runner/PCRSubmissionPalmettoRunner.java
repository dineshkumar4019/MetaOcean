package runner;


import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features="src\\test\\resources\\features\\PCRSubmissionPalmetto.feature",
                glue ="PCRSubmissionPalmetto",
                dryRun = false,
                monochrome = true,
                plugin= {"pretty", "json:target/CuCu/Report.json"},
                tags = "@Test")
public class PCRSubmissionPalmettoRunner {

}
