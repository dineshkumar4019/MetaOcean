package updatePCRStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.Assert;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inputData.TestDatas;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import logger.FunctionalBlockLogger;

public class UpdatePCRStatusStepDefinitions {
	private String tokenGenerated;
	private Response response;
	String output;
	String validateTestCase;
	
	Properties props = new Properties();
	CountSubString countSubString = new CountSubString();
	private static final FunctionalBlockLogger logger = new FunctionalBlockLogger(UpdatePCRStatusStepDefinitions.class);
	private final String TESTCASES = "inputs"; // JSON key contains multiple testCase object
	private TestDatas testDatas = TestDatas.getTestDatasInstance("\\src\\test\\resources\\config\\TestData.json");
	private TestDatas expectedSuccessOutput = TestDatas.getTestDatasInstance("\\src\\test\\resources\\JSON\\ExpectedSuccessOutput.json");
	
	private final String EXPECTEDSTATUSCODE = testDatas.getIfValueAsString("pass"); 
	private final String EXPECTEDSUCCESSCODE = testDatas.getIfValueAsString("success");
	
	@Given("User login with Valid {string} and {string}")
	public void user_login_with_valid_and(String username, String password) throws Exception {
		final String TOPIC = testDatas.getIfValueAsString("topic");
		
		props.put("bootstrap.servers", "\r\n"
				+ testDatas.getIfValueAsString("server"));
		props.put("group.id", "test");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", testDatas.getIfValueAsString("key"));
		props.put("value.deserializer", testDatas.getIfValueAsString("key"));
		
		KafkaConsumer<String,String> consumer = new KafkaConsumer<String,String>(props);
		consumer.subscribe(Collections.singleton(TOPIC));
		
		while(true) {
			ConsumerRecords<String,String> record = consumer.poll(Duration.ofMillis(20000));
			logger.info("Test Partitions " + record.partitions());
			logger.info("Count : " + record.count());
			
			for (ConsumerRecord<String,String> recordList:record) {
				output = recordList.value();
				logger.info("Initial records : " + output);
				
			}
			consumer.close();
			break;
		}
		
		JsonObject requestParams = new JsonObject();
		RestAssured.baseURI = testDatas.getIfValueAsString("URI");
		
		requestParams.addProperty("usernameOrEmailAddress", testDatas.getIfValueAsString(username));
		requestParams.addProperty("tenancyName", "Default");
		requestParams.addProperty("password", testDatas.getIfValueAsString(password));

		logger.info("Request params - " + requestParams.toString());
		response = RestAssured.given()
				.header("Content-type", "application/json")
                .and()
                .body(requestParams.toString())
                .when()
                .post(testDatas.getIfValueAsString("Request1"))
                .then()
                .extract().response();
		String responseBody = response.getBody().asString();
		String reponseStatusCode = Integer.toString(response.statusCode());
		
		logger.info("Entering @Given Block");
		logger.info("Response Body is:" + responseBody.toString());
		logger.info("Status Code - " + reponseStatusCode + " Vs " + EXPECTEDSTATUSCODE
				+ "Respones code Vs Expected code") ;
		
		Assert.assertEquals(reponseStatusCode, EXPECTEDSTATUSCODE);
		tokenGenerated = JsonPath.from(responseBody).get("result");
		logger.info("Generated Token - " + tokenGenerated);
	}
	
	@When("User Triggers the UpdatePCRStatus Functionalblock with the given {string}")
	public void user_triggers_the_update_pcr_status_functionalblock_with_the_given(String testCaseInput) throws Exception {
		logger.info("Entering @When Block with testcase - " + testCaseInput);
		RestAssured.baseURI = testDatas.getIfValueAsString("URI");
		validateTestCase = testDatas.getAsJsonObject(TESTCASES + ", " + testCaseInput);
		
		response = RestAssured.given()
				.header("Authorization", "Bearer " + tokenGenerated)
				.header("Content-Type", "application/json")
				.header("X-UIPATH-OrganizationUnitId", "10")
				.and()
				.body(validateTestCase)
				.when()
				.post(testDatas.getIfValueAsString("Request2"))
				.then().extract().response();
		String responseBody = response.getBody().asString();
		String reponseStatusCode = Integer.toString(response.statusCode());
		
		logger.info("Response Body is:" + responseBody.toString());
		logger.info("Status Code - " + reponseStatusCode + " Vs " + EXPECTEDSTATUSCODE
				+ "Respones code Vs Expected code");
		
		Assert.assertEquals(reponseStatusCode, EXPECTEDSUCCESSCODE);
				
				
	}
	@Then("User checks the status of the job with the status code {string}")
	public void user_checks_the_status_of_the_job_with_the_status_code(String code) throws SecurityException, IOException {
		List<String> results = new ArrayList<>(Arrays.asList(code.split("\\s*,\\s*")));
		int count = countSubString.countMatches(validateTestCase, "patient_identifier");
		logger.info("Entering @Then Block");
		
		KafkaInboundEndpoint<String> endPoint = new KafkaInboundEndpoint<String>(props);
		ITaskHandler<List<String>> taskHandler = new ITaskHandler<List<String>>() {

			@Override
			public void perform(List<String> messages) {
				logger.info("initial Output -- " + messages );
				try {
					Thread.sleep(90000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < messages.size(); i++) {
					logger.info("finalOutputs for index " + i + " is " + messages.get(i));
					
					String successOutputJson = expectedSuccessOutput.getAsJsonObject().toString();
					JsonElement responseJson = JsonParser.parseString(messages.get(i));
					
					JSONObject jsonObject = new JSONObject(successOutputJson); //As schema loader accepts only JSONObject not JsonObject 
					try {
						Schema schemaValidator = SchemaLoader.load(jsonObject);
						schemaValidator.validate(responseJson);
						logger.info("Response Json Is Valid");
					} catch (Exception e) {
						logger.error("Exception occurred ");
						logger.error("Input JSON Is NOT VALID");
					}
					
					TestDatas responseData = TestDatas.getTestDatasInstance(responseJson);
					
					if (results.get(i).equalsIgnoreCase("Success")) {
						logger.info("Assertion for input - " + results.get(i));
						Assert.assertEquals(responseData.getAsJsonObject("payload, status"), "Success");
					} else {
						logger.info("Assertion for input - " + results.get(i));
						Assert.assertEquals(responseData.getAsJsonObject("payload, error, error_message"),
								testDatas.getIfValueAsString(results.get(i)));			
					}
				}
			}
		
		};
		endPoint.start(taskHandler, count, testDatas.getIfValueAsString("topic"));
	}
}