package constants;

public class FBConstants {
	public final static String TESTDATAPATH = "\\src\\test\\resources\\config\\TestData.json";
	public final static String EXPECTEDJSONPATH = "\\src\\test\\resources\\JSON\\ExpectedSuccessOutput.json";
	
	public final static String RESPONSEERRORMESSAGEJSONKEY = "payload, error, error_message"; //keys to get error message form kafka json response
	public final static String RESPONSESTATUSJSONKEY = "payload, status";
	public final static String TESTCASESJSONKEY = "inputs"; // JSON key contains multiple testCase object in testDataJson
	
	public final static String SUCCESS = "Success";
	public final static String PATIENTIDENTIFIER = "patient_identifier";
}
