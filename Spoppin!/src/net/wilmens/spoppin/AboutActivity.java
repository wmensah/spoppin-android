package net.wilmens.spoppin;

import java.util.ArrayList;
import java.util.List;

import net.wilmens.spoppin.utilities.StringUtils;
import net.wilmens.spoppin.utilities.UIUtils;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutActivity extends BaseSpoppinActivity {

	Context mContext;
	
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_about);
		   
	    // allow navigating up with the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("About");

        mContext = this;
        
        TextView vAppVersion = (TextView)findViewById(R.id.txtAppVersion);
        try {
			String versionName = context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionName;

	        if (!StringUtils.isNullOrEmpty(versionName)){
	        	vAppVersion.setText("v" + versionName + " (beta)");
	        }
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			vAppVersion.setVisibility(View.GONE);
		}
	    
	    ListView list = (ListView)findViewById(R.id.lvAboutItems);
	    List<Item> items = new ArrayList<Item>();
	    
	    items.add(new Item(getString(R.string.text_help)));
	    items.add(new Item(getString(R.string.text_rateapp)));
	    items.add(new Item(getString(R.string.text_privacy_policy)));
	    items.add(new Item(getString(R.string.text_terms_conditions)));
	    
	    list.setAdapter(new AboutListAdapter(this, R.layout.info_window_layout, items));
	    list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String url = "http://spoppin.com";
				switch(position){
				case 0:
					// Help
					url += "/docs/en/help.html";
					break;
				case 1:
					// rate app
					url = "market://details?id=" + context.getPackageName();
					break;
				case 2:
					// Privacy Policy
					url += "/docs/en/privacy.html";
					break;
				case 3:
					// Terms
					url += "/docs/en/terms.html";
					break;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
	    	
	    });
	}

}

final class AboutListAdapter extends ArrayAdapter<Item> {

	Context mContext;
	int layoutResourceId;
	List<Item> items = null;
	

	public AboutListAdapter(Context context, int resourceId, List<Item> items) {
		super(context, resourceId, items);
	    this.mContext = context;
	    this.layoutResourceId = resourceId;
	    this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
	    View v = convertView;
	
	    if (v == null) {
	
	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(this.layoutResourceId, null);
	        TextView tv = (TextView)v.findViewById(R.id.txtMarkerTitle);
	        if (tv != null){
	        	tv.setPadding(0, 20, 0, 20);
	        	tv.setTextSize(18);
	        }
	        tv.setText(((Item)this.items.get(position)).getText());
	    }
	    return v;
	}
}
