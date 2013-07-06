package com.example.spoppin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
			lblConnectionStatus.setVisibility(CheckInternet()? View.GONE : View.VISIBLE);
	}
	
	@Override
    public void setContentView(int id) {
		LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(id, linBase);
    }
	
	protected boolean CheckInternet() 
	{
	    ConnectivityManager connec = (ConnectivityManager) ((Activity) this).getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if (wifi.isConnected()) {
	        return true;
	    } else if (mobile.isConnected()) {
	        return true;
	    }
	    return false;
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

}
