package net.wilmens.spoppin;

public class APIHelper {

	private static String WEBSERVICE_ENDPOINT_PROD = "http://spoppin.com/api/"; 
	private static String WEBSERVICE_ENDPOINT_DEV = "http://localhost/spoppin-web/api/";
	private static String APIKey = "123"; // TODO: utilize
	private static Boolean debug = false;
	
	public static String getWebserviceUrl(){
		String endpoint = null;
		if (debug)
			endpoint = WEBSERVICE_ENDPOINT_DEV;
		else
			endpoint = WEBSERVICE_ENDPOINT_PROD;
		return endpoint + "key/" + APIKey + "/";
	}
}
