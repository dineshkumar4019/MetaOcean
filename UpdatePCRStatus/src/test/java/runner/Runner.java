package runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features="src\\test\\resources\\features\\UpdatePCRStatus.feature",
                glue ="updatePCRStatus",
                dryRun = false,
                monochrome = true,
                plugin= {"pretty", "json:target/CuCu/Report.json"},
                tags = "@NoPatient")
public class Runner {
	
}
