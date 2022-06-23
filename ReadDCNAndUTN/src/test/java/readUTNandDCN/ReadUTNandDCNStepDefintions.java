package readUTNandDCN;

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

import constants.FBConstants;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import logger.FunctionalBlockLogger;
import readJSONValue.ReadJSONData;


public class ReadUTNandDCNStepDefintions {
	private String tokenGenerated;
	private Response response;
	private List<String> testCaseKeys;
	
	private Properties props = new Properties();
	private CountSubString countSubString = new CountSubString();
	private static final FunctionalBlockLogger logger = new FunctionalBlockLogger(ReadUTNandDCNStepDefintions.class);
	private ReadJSONData testDatas = ReadJSONData.getTestDatasInstance(FBConstants.TESTDATAPATH);
	private ReadJSONData expectedSuccessOutput = ReadJSONData.getTestDatasInstance(FBConstants.EXPECTEDJSONPATH);
	
	private final String EXPECTEDSTATUSCODE = testDatas.getIfValueAsString("pass"); 
	private final String EXPECTEDSUCCESSCODE = testDatas.getIfValueAsString("success");
	
	@Given("User login with Valid {string} and {string}")
	public void user_login_with_valid_and(String username, String password) throws Exception {
		logger.info("-------------LOGGER STARTED-------------");
		JsonObject requestParams = new JsonObject();
		
		props.put("bootstrap.servers", "\r\n"
				+ testDatas.getIfValueAsString("server"));
		props.put("group.id", "test");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", testDatas.getIfValueAsString("key"));
		props.put("value.deserializer", testDatas.getIfValueAsString("key"));
		
		KafkaConsumer<String,String> consumer = new KafkaConsumer<String,String>(props);
		consumer.subscribe(Collections.singleton(testDatas.getIfValueAsString("topic")));
		
		while(true) {
			ConsumerRecords<String,String> record = consumer.poll(Duration.ofMillis(20000));
			logger.info("Test Partitions " + record.partitions());
			logger.info("Count : " + record.count());
			
			for (ConsumerRecord<String,String> recordList:record) {
				logger.info("Initial records - " + recordList.value());
			}
			consumer.close();
			break;
		}
		logger.info("Entering @Given Block");
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
                .post(testDatas.getIfValueAsString("authenticateRequest"))
                .then()
                .extract().response();
		String responseBody = response.getBody().asString();
		String reponseStatusCode = Integer.toString(response.statusCode());
		
		logger.info("Response Body is:" + responseBody.toString());
		logger.info("Status Code - " + reponseStatusCode + " Vs " + EXPECTEDSTATUSCODE
				+ " Response code Vs Expected code") ;
		
		if (EXPECTEDSTATUSCODE.equals(reponseStatusCode)) {
			tokenGenerated = JsonPath.from(responseBody).get("result");
			logger.info("Generated Token - " + tokenGenerated);
		} else  {
			logger.fatal("FATAL ERROR in Generating Token - " + JsonPath.from(responseBody).get("message"));
			Assert.assertEquals(EXPECTEDSTATUSCODE, reponseStatusCode);
		}
	}
	
	@When("User Triggers the ReadUTNandDCN Functionalblock with the given {string}")
	public void user_triggers_the_read_utn_and_dcn_functionalblock_with_the_given(String testCaseInputs) throws Exception {
		logger.info("Entering @When Block");
		String testcaseInput;
		String addQueueRequest = testDatas.getIfValueAsString("addQueueRequest");
		testCaseKeys = Arrays.asList(testCaseInputs.split("\\s*,\\s*"));
		RestAssured.baseURI = testDatas.getIfValueAsString("URI");
		
		for(int i = 0; i < testCaseKeys.size(); i++) {
			testcaseInput = testCaseKeys.get(i);
			logger.info("Adding TestCase to Queue - " + testcaseInput);
		
			response = RestAssured.given()
					.header("Authorization", "Bearer " + tokenGenerated)
					.header("Content-Type", "application/json")
					.header("X-UIPATH-OrganizationUnitId", "10")
					.and()
					.body(testDatas.getAsJsonObject(FBConstants.TESTCASESJSONKEY + ", " + testcaseInput))
					.when()
					.post(addQueueRequest)
					.then().extract().response();
		}
		String responseBody = response.getBody().asString();
		String reponseStatusCode = Integer.toString(response.statusCode());
		
		logger.info("Response Body is:" + responseBody.toString());
		logger.info("Status Code - " + reponseStatusCode + " Vs " + EXPECTEDSTATUSCODE
				+ "Respones code Vs Expected code");
		
		Assert.assertEquals(reponseStatusCode, EXPECTEDSUCCESSCODE);
	}
	
	@Then("User checks the status of the job with the status code {string}")
	public void user_checks_the_status_of_the_job_with_the_status_code(String code) throws SecurityException, IOException {
		logger.info("Entering @Then Block");
		int count = testCaseKeys.size();
		int specificCount;                        //specificCount contains no.of.patients in single input
		List<String> results = new ArrayList<>(Arrays.asList(code.split("\\s*,\\s*")));
		KafkaInboundEndpoint<String> endPoint = new KafkaInboundEndpoint<String>(props);
		logger.info("Number of Inputs - " + count);
		
		ITaskHandler<List<String>> taskHandler = new ITaskHandler<List<String>>() {

			@Override
			public void perform(List<String> messages, int currentInputIndex) {
				ReadJSONData responseData;
				JsonElement responseJson;
				JSONObject jsonObject;
				Schema schemaValidator;
				
				String kafkaMessage;
				String successOutputJson;
				String currentInput = testCaseKeys.get(currentInputIndex);
				String currentInputExpectedStatus = results.get(currentInputIndex);
			    
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < messages.size(); i++) {
					kafkaMessage = messages.get(i);
					successOutputJson = expectedSuccessOutput.getAsJsonObject().toString();
					responseJson = JsonParser.parseString(kafkaMessage);
					jsonObject = new JSONObject(successOutputJson); //As schema loader accepts only JSONObject not JsonObject type
					logger.info("final Output of index " + i + " is " + kafkaMessage);
					
					try {
						schemaValidator = SchemaLoader.load(jsonObject);
						schemaValidator.validate(responseJson);
						logger.info("Response Json Is Valid");
					} catch (Exception e) {
						logger.error("Exception occurred - Input JSON Is NOT VALID ");
					}
					
				    responseData = ReadJSONData.getTestDatasInstance(responseJson);
					logger.info("Response - " + responseData.toString());
					
					try {
						if (currentInputExpectedStatus.equalsIgnoreCase("Success")) {
							logger.info("Asserting for input - " + currentInput + " , Expecting - SUCCESS in response");
							logger.info("Assertion between - " 
									+ FBConstants.SUCCESS
									+ " VS " +  responseData.getAsJsonObject(FBConstants.RESPONSESTATUSJSONKEY));
							Assert.assertEquals(FBConstants.SUCCESS, responseData.getAsJsonObject(FBConstants.RESPONSESTATUSJSONKEY));
						} else {
							logger.info("Asserting for input - " + currentInput + " , Expecting - ERROR in response");	
							logger.info("Assertion between - " 
									+ responseData.getAsJsonObject(FBConstants.RESPONSEERRORMESSAGEJSONKEY) 
									+ " VS " +  testDatas.getAsJsonObject(currentInputExpectedStatus));
							Assert.assertEquals(responseData.getAsJsonObject(FBConstants.RESPONSEERRORMESSAGEJSONKEY),
									testDatas.getAsJsonObject(currentInputExpectedStatus));
						}
						logger.info("**************Assertion PASSED for the Input - "  + currentInput);
					} catch (AssertionError e) {
						logger.error("**************Assertion FAILED for the input - " + currentInput);
					}
				}
			}
		};
		
		for (int currentInput = 0; currentInput < count; currentInput++ ) {
			logger.info("--------Reading Kafka for input - " + testCaseKeys.get(currentInput));
			specificCount = countSubString.countMatches(testDatas.getAsJsonObject(FBConstants.TESTCASESJSONKEY + ", " + testCaseKeys.get(currentInput))
					, FBConstants.PATIENTIDENTIFIER);
			endPoint.start(taskHandler, specificCount, testDatas.getIfValueAsString("topic"), currentInput);
		}
		logger.info("-------------LOGGEER ENDED--------------");
	}
}
