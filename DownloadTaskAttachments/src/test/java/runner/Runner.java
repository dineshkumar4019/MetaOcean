package runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features="src\\test\\resources\\features\\DownloadTaskAttachments.feature",
                glue ="downloadTaskAttachments",
                dryRun = false,
                monochrome = true,
                plugin= {"pretty", "json:target/CuCu/Report.json"})
public class Runner {
	
}
