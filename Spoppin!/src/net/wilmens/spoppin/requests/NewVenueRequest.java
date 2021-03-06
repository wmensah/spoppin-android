package net.wilmens.spoppin.requests;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.wilmens.spoppin.APIHelper;
import net.wilmens.spoppin.RequestParameter;
import net.wilmens.spoppin.objects.ServerResponseEnum;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.net.Uri;

public class NewVenueRequest extends Request {
	NewVenueResponse response;
	
	public NewVenueRequest(Activity requestingPage) {
		super(requestingPage);
	}

	@Override
	public void buildRequest(List<RequestParameter> params) {
		super.buildRequest("requestvenue", params);	
	}

	@Override
	protected void handleResponse(Object result) throws JSONException {
		response = new NewVenueResponse();
		Response resval = (Response)result;
		response.result = resval.result;
		response.success = resval.success;

		if (resval.success){
			if (resval.result == ServerResponseEnum.OK)
				response.success = true;
			else{
				JSONObject json;
				json = new JSONObject(resval.data);
				response.errorMessage = json.getString("status");
			}
		}else{
			response.success = false;
			response.errorMessage = resval.errorMessage;
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
