package com.example.spoppin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseSpoppinActivity {
	
	private ListView lv;

	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        
        Log.w("spoppin", "started");
        
        final BarRank[] bar_rank_data = new BarRank[]{
        		new BarRank(R.drawable.ic_launcher, "Club Z", 20, 1),
        		new BarRank(-1, "Chasers", 23, 2),
        		new BarRank(-1, "Bent Willy's", 115, 3)        		
        };
        
        BarRankAdapter adapter = new BarRankAdapter(this, R.layout.list_item, bar_rank_data);
        
        lv = (ListView)findViewById(R.id.lstBars);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new OnItemClickListener(){
        	
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		SpopPrompt(position, bar_rank_data[position].name);
        		//Toast.makeText(MainActivity.this, bar_rank_data[position].name, Toast.LENGTH_LONG).show();
        	}     
        });
    }
	

    
    private void SpopPrompt(int venueId, final String venueName){
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setMessage(String.format(getString(R.string.spop_prompt_message), venueName));
    	builder.setTitle(R.string.spop_prompt_title);
    	builder.setPositiveButton(R.string.spoppin, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ShowToast(venueName + " " + getString(R.string.spoppin).toLowerCase()+"!", true);
			}
		});
    	builder.setNegativeButton(R.string.sucks, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ShowToast(venueName + " " + getString(R.string.sucks).toLowerCase()+"!", true);
			}
		});
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
    
    private void ShowToast(String message, Boolean lng){
    	Toast.makeText(MainActivity.this, message, lng? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        //menu.add(0, MENU_REQUEST_VENUE, Menu.NONE, R.string.menu_venue_request);
        //menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	Intent i = null;
        switch(item.getItemId())
        {
            case R.id.menu_settings:
			  // open settings page
			  //i = new Intent(this, SettingsActivity.class);
			  //this.startActivity(i);
			  return true;
            case R.id.menu_venue_request:
				// open venue request page
				i = new Intent(this, VenueRequestActivity.class);
				this.startActivity(i);
				return true;
            default:
                  return super.onOptionsItemSelected(item);
        }
    }
    
}
