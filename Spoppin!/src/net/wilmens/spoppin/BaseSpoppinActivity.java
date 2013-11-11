package net.wilmens.spoppin;

import net.wilmens.spoppin.objects.PreferencesManager;
import net.wilmens.spoppin.objects.ServerResponseEnum;
import net.wilmens.spoppin.utilities.ConnectionUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.wilmens.spoppin.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class BaseSpoppinActivity extends ActionBarActivity {

	RelativeLayout linBase;
	ProgressView progressView;
	MainApp context;
	SlidingMenu menu;
	PreferencesManager pm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_spoppin_base);
		context = (MainApp)this.getApplicationContext(); 
		
		linBase = (RelativeLayout)findViewById(R.id.linBase);
		progressView = (ProgressView)findViewById(R.id.pvVenueRequest);
		progressView.setVisibility(View.INVISIBLE); // will show when needed
		
		pm = new PreferencesManager(context);
		
		if (this instanceof INavigationMenu){
			menu = new SlidingMenu(this);
	        menu.setMode(SlidingMenu.LEFT);
	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	        menu.setShadowWidthRes(R.dimen.shadow_width);
	        menu.setShadowDrawable(R.drawable.shadow);
	        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	        menu.setFadeDegree(0.35f);
	        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	        menu.setMenu(R.layout.menu);
	        menu.setBackgroundColor(Color.parseColor("#6A287E"));
		}
        
		init();
	}

	@Override
	public void onResume()
	{
		if (menu != null && menu.isMenuShowing()) {
	        menu.showContent(true);
	    }
	    super.onResume();
	}
	
	@Override
	// If the back button is pressed and the sliding menu is open, close it.
	public void onBackPressed() {
	    if (menu != null && menu.isMenuShowing()) {
	        menu.showContent(true);
	        return;
	    }

	    super.onBackPressed();
	}
	
	@Override
	// If the menu button (on the phone) is pressed, open the sliding menu
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	    	if (menu != null && !menu.isMenuShowing()) {
		        menu.showMenu(true);
		    }
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	// Initialize UI elements
	protected void init(){
		TextView lblConnectionStatus = (TextView)findViewById(R.id.lblConnectionStatus);
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
	
	/**
	 * Performs a number of actions common to all activities based on a response
	 * from the server such as displaying the result message as a Toast.
	 * @param response The response from the server
	 */
	protected void PreProcessServerResponse(ServerResponseEnum response){
		String msg = null;
		switch(response){
			case OK:
				break;
			case RequestTimeout:
				msg = getString(R.string.msg_request_timed_out);
				break;
			case NotConnected:
				msg = getString(R.string.msg_no_network_connection);
				break;
			default:
				msg = getString(R.string.msg_no_server_communication);
				break;
		}
		if (msg != null){
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}

}
