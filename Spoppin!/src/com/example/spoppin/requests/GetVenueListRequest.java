package com.example.spoppin.requests;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.example.spoppin.RequestParameter;
import com.example.spoppin.objects.ServerResponseEnum;
import com.example.spoppin.objects.Venue;

public class GetVenueListRequest extends Request {

	GetVenueListResponse response;
	
	public GetVenueListRequest(Activity requestingPage) {
		super(requestingPage);
	}

	@Override
	public void buildRequest(List<RequestParameter> params) {
		super.buildRequest("getvenues", params);	
	}

	@Override
	protected void handleResponse(Object result) throws JSONException {
		response = new GetVenueListResponse();
		Response resval = (Response)result;
		response.result = resval.result;
		response.success = resval.success;
		
		if (resval.success){
			
			JSONObject json = new JSONObject(resval.data);
			
			if (resval.result == ServerResponseEnum.OK){
				response.success = true;
				
				this.response.venues = new ArrayList<Venue>();
				JSONArray varray = json.getJSONArray("data"); // array of venues
				
				for(int i = 0; i < varray.length(); i++){
					Venue v = null;
					try {
						v = Venue.loadFromJson((JSONObject) varray.get(i));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (v != null)
						this.response.venues.add(v);
				}
			}
			else{
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
	
	public GetVenueListResponse getResponse(){
		return this.response;
	}

}
