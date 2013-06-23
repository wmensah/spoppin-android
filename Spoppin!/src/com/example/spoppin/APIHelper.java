package com.example.spoppin;

public class APIHelper {

	private static String WEBSERVICE_ROOT_URL = "http://spoppin.com/api/";
	private static String APIKey = "123"; // not used yet
	public static String getWebserviceUrl(){
		return WEBSERVICE_ROOT_URL + "/?key=" + APIKey + "&";
	}
}
