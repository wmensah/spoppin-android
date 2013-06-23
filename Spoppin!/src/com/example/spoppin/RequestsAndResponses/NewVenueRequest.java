package com.example.spoppin.RequestsAndResponses;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.spoppin.APIHelper;
import com.example.spoppin.RequestParameter;
import com.example.spoppin.ServerResponseEnum;

import android.app.Activity;
import android.net.Uri;

public class NewVenueRequest extends Request {
	NewVenueResponse response;
	
	public NewVenueRequest(Activity requestingPage) {
		super(requestingPage);
	}

	@Override
	public void buildRequest(List<RequestParameter> params) {
		super.buildRequest("venuerequest", params);	
	}

	@Override
	protected void handleResponse(Object result) throws JSONException {
		response = new NewVenueResponse();
		Response resval = (Response)result;
		if (resval.result == ServerResponseEnum.OK)
			response.success = true;
		else{
			JSONObject json;
			json = new JSONObject(resval.data);
			response.errorMessage = json.getString("status");
		}
		super.doHandleResponse();
	}

	@Override
	public void setResponseHandler(Method responseHandler) {
		this.responseHandler = responseHandler;		
	}
	
	public NewVenueResponse getResponse(){
		return this.response;
	}
}
