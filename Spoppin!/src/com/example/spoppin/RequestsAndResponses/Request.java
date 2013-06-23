package com.example.spoppin.RequestsAndResponses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.spoppin.ServerResponseEnum;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public abstract class Request extends AsyncTask<Object, Object, Object>{
	private Uri requestUri;
	protected Activity requestingPage;
	protected Method responseHandler;
	
	public Request(Activity requestingPage){
		this.requestingPage = requestingPage;
	}
	
	protected abstract void buildRequest();
	protected abstract void handleResponse(Object result) throws JSONException;
	protected void setRequestUri(String url){
		requestUri = Uri.parse(url);
	}
	protected void setRequestUri(Uri uri){
		requestUri = uri;
	}
	public abstract void setResponseHandler(Method responseHandler);
	
	public boolean IsValid(){
		if (requestUri == null || requestUri.toString().length() == 0)
			return false;
		return true;
	}
	
	@Override
	protected Object doInBackground(Object... arg0){
		buildRequest();
		
		if (!IsValid())
			return null;
			
		Log.w("spoplog", "Sending request: " + requestUri.toString());
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpPost httppost = new HttpPost(this.requestUri.toString());
		// Depends on your web service
		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
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
		Response resval = new Response();
		resval.data = result;
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
		
		if (this.requestUri.getQueryParameter("request").equals(request) &&
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
}
