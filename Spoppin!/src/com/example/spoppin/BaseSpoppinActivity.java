package com.example.spoppin;

import SpoppinObjects.ServerResponseEnum;
import Utilities.ConnectionUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseSpoppinActivity extends Activity {

	LinearLayout linBase;
	ProgressView progressView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_spoppin_base);
		
		linBase = (LinearLayout)findViewById(R.id.linBase);
		progressView = (ProgressView)findViewById(R.id.pvVenueRequest);
		progressView.setVisibility(View.INVISIBLE); // will show when needed
		
		TextView lblConnectionStatus = (TextView)findViewById(R.id.lblConnectionStatus);
		if (lblConnectionStatus != null)
			lblConnectionStatus.setVisibility(ConnectionUtils.isConnected(this)? View.GONE : View.VISIBLE);
	}
	
	@Override
    public void setContentView(int id) {
		LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(id, linBase);
    }
	
	protected void ShowOkDialog(String title, String message, DialogInterface.OnClickListener action){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	builder.setCancelable(true);
    	if (action == null)
    		action = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            };
        builder.setNeutralButton(android.R.string.ok, action);
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
	
	protected void SetProgressLabelText(String text, Boolean show){
		if (progressView != null)
	    	progressView.setLabelText(text);
		if (show)
			progressView.setVisibility(View.VISIBLE);
	}
	
	protected void HandleServerResponse(ServerResponseEnum response){
		String msg = null;
		switch(response){
			case OK:
				break;
			case RequestTimeout:
				msg = "Request timed out";
				break;
			case NotConnected:
				msg = "No Internet connection";
				break;
			default:
				msg = "Unable to communicate with server";
				break;
		}
		if (msg != null){
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}

}
