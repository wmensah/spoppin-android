package net.wilmens.spoppin.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

import net.wilmens.spoppin.APIHelper;
import net.wilmens.spoppin.RequestParameter;
import net.wilmens.spoppin.objects.ServerResponseEnum;
import net.wilmens.spoppin.utilities.ConnectionUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


public abstract class Request extends AsyncTask<Object, Object, Object>{
	private Uri requestUri;
	private List<RequestParameter> parameters;
	protected Activity requestingPage;
	protected Method responseHandler;
	
	public Request(Activity requestingPage){
		this.requestingPage = requestingPage;
	}
	
	protected abstract void buildRequest(List<RequestParameter> params);
	protected abstract void handleResponse(Object result) throws JSONException;
	public abstract void setResponseHandler(Method responseHandler);
	
	public boolean RequestIsValid(){
		if (requestUri == null || requestUri.toString().length() == 0)
			return false;
		return true;
	}
	
	@Override
	protected Object doInBackground(Object... arg0){
		Response resval = new Response();
		resval.success = false;
		
		// Validate the request
		if (!RequestIsValid())
			return null;
		
		// Check Internet connection
		if (!ConnectionUtils.isConnected(requestingPage)){
			resval.result = ServerResponseEnum.NotConnected;
			return resval;
		}
			
		Log.w("spoplog", "Sending request: " + requestUri.toString());
		
		// Set HTTP parameters
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000; // 3 seconds (in miliseconds)
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000; 
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httppost = new HttpPost(this.requestUri.toString());
		// Depends on your web service
		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		HttpResponse response = null;
		
		try {
			response = httpclient.execute(httppost);
		} catch (SocketTimeoutException e){
			resval.result = ServerResponseEnum.RequestFailed;
			resval.errorMessage = e.getMessage();
			return resval;
		} catch (Exception e){
			e.printStackTrace();
			resval.result = ServerResponseEnum.Unknown;
			resval.errorMessage = e.getMessage();
			return resval;
		}
		HttpEntity entity = response.getEntity();

		try {
			inputStream = entity.getContent();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null)
			{
			    sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = sb.toString();
		Log.w("spoplog", result);
		
		resval = new Response();
		resval.data = result;
		resval.success = true;
		try {
			resval.result = CheckResult(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resval;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		try {
			this.handleResponse(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private ServerResponseEnum CheckResult(String result) throws JSONException{
		JSONObject json;
		json = new JSONObject(result.toString());
		
		String status = json.get("status").toString();
		String request = (String) json.get("request").toString();
		
		if ((this.requestUri.toString().indexOf(request) > 0) &&
				status.equals("200")){
			return ServerResponseEnum.OK;
		}
		return ServerResponseEnum.Unknown;
	}
	
	public void sendRequest(){
		this.execute();
	}
	
	public void doHandleResponse(){
		if (responseHandler != null){
			try {
				responseHandler.invoke(this.requestingPage);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected void buildRequest(String request, List<RequestParameter> params){
		this.parameters = params;
		Uri.Builder b = Uri.parse(APIHelper.getWebserviceUrl()).buildUpon();
		b.appendPath(request);
		
		// Append the parameters
		for (int i = 0; i < params.size(); i++){
			b.appendPath(params.get(i).Key);
			b.appendPath(params.get(i).Value);
		}
		
		requestUri = b.build();
	}
	
	public String getParameterValue(String key){
		if (this.parameters == null || this.parameters.size() == 0){
			return null;
		}
		for(RequestParameter p:this.parameters){
			if (p.Key == key){
				return p.Value;
			}
		}
		return null;
	}
}
