package net.wilmens.spoppin;

import net.wilmens.spoppin.objects.VenueMarker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuListFragment extends ListFragment{
	
	private MainActivity activity;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onAttach(Activity activity){
    	if (!(activity instanceof MainActivity)){
    		throw new IllegalStateException("Must be attached to an instance of MainActivity");
    	}
    	this.activity = (MainActivity) activity;
    	super.onAttach(activity);
    }
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SampleAdapter adapter = new SampleAdapter(getActivity());
        
        adapter.add(new SlidingMenuItem("Map", R.drawable.ic_action_place));
        adapter.add(new SlidingMenuItem("Settings", R.drawable.ic_action_settings));
        adapter.add(new SlidingMenuItem("About", R.drawable.ic_action_about));
        
        setListAdapter(adapter);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		
		Intent i = null;
        switch(position){
        	case 0:
        		//Map
        		i = new Intent(this.getActivity().getApplicationContext(), VenueMapActivity.class);
        		i.putExtra("venues", VenueMarker.VenueListToVenueMarkerList(this.activity.venueList));
        		this.startActivity(i);
        		break;
        	case 1:
        		//Settings
        		i = new Intent(this.getActivity().getApplicationContext(), SettingsActivity.class);
        		this.startActivity(i);
        		break;
        	case 2:
        		//About
        		i = new Intent(this.getActivity().getApplicationContext(), AboutActivity.class);
        		this.startActivity(i);
        		break;
        }
    }

	private class SlidingMenuItem {
	        public String tag;
	        public int iconRes;
	        public SlidingMenuItem(String tag, int iconRes) {
	                this.tag = tag; 
	                this.iconRes = iconRes;
	        }
	}

	public class SampleAdapter extends ArrayAdapter<SlidingMenuItem> {
	
	        public SampleAdapter(Context context) {
	                super(context, 0);
	        }
	
	        public View getView(int position, View convertView, ViewGroup parent) {
	                if (convertView == null) {
	                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
	                }
	                ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
	                icon.setImageResource(getItem(position).iconRes);
	                TextView title = (TextView) convertView.findViewById(R.id.row_title);
	                title.setText(getItem(position).tag);
	
	                return convertView;
	        }
	
	}
}
