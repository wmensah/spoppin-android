package net.wilmens.spoppin;

import java.util.ArrayList;

import net.wilmens.spoppin.objects.PreferencesManager;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BarRankAdapter extends ArrayAdapter<BarRank> {

	Context context;
	int layoutResourceId;
	ArrayList<BarRank> data = null;
	
	public BarRankAdapter(Context context, int layoutResourceId, ArrayList<BarRank> venueList){
		super(context, layoutResourceId, venueList);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = venueList;	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BarRankHolder holder = null;
		
		if (row == null){
			LayoutInflater inflater = 
					((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new BarRankHolder();
			holder.lblName = (TextView)row.findViewById(R.id.lblVenueName);
			holder.lblRank = (TextView)row.findViewById(R.id.lblRank);
			holder.lblDrinks = (TextView)row.findViewById(R.id.lblDrinks);
			holder.lblMusic = (TextView)row.findViewById(R.id.lblMusic);
			holder.lblGirls = (TextView)row.findViewById(R.id.lblGirls);
			holder.lblGuys = (TextView)row.findViewById(R.id.lblGuys);
			holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
			holder.imgTopCategory = (ImageView)row.findViewById(R.id.imgTopCategory);
			
			PreferencesManager pm = new PreferencesManager(context);
			LinearLayout stats = (LinearLayout)row.findViewById(R.id.llVenueRankCategories);
			stats.setVisibility(pm.getUserPreferences().getShowStatistics()? View.VISIBLE : View.GONE);
			if (!pm.getUserPreferences().getShowStatistics()){
				row.setPadding(0, 20, 0, 20);
			}else{
				row.setPadding(0, 15, 0, 15);
			}
			
			row.setTag(holder);			
		}else{
			holder = (BarRankHolder)row.getTag();
		}
		
		BarRank barRank = data.get(position);
		holder.lblName.setText(barRank.venue.getName());
		holder.lblRank.setText(Integer.toString(barRank.rank));
		holder.lblDrinks.setText("drinks:" + Double.toString(barRank.score.getDrinks()));
		holder.lblMusic.setText("music:" + Double.toString(barRank.score.getMusic()));
		holder.lblGirls.setText("girls:" + Double.toString(barRank.score.getGirls()));
		holder.lblGuys.setText("guys:" + Double.toString(barRank.score.getGuys()));
		holder.imgIcon.setImageResource(barRank.icon);
		switch(barRank.score.getBestCategory()){
			case Drinks:
				holder.imgTopCategory.setImageResource(R.drawable.drinks_off);
				break;
			case Music:
				holder.imgTopCategory.setImageResource(R.drawable.music_off);
				break;
			case Girls:
				holder.imgTopCategory.setImageResource(R.drawable.girls_off);
				break;
			case Guys:
				holder.imgTopCategory.setImageResource(R.drawable.guys_off);
				break;
			default:
				holder.imgTopCategory.setImageResource(R.drawable.ic_launcher); //TODO: replace this icon
		}
		
		if (barRank.rank > 1){
			holder.imgIcon.setVisibility(0);
		}
		
		return row;		
	}

	static class BarRankHolder{
		ImageView imgIcon;
		ImageView imgTopCategory;
		TextView lblRank;
		TextView lblName;
		TextView lblDrinks;
		TextView lblMusic;
		TextView lblGirls;
		TextView lblGuys;
	}
	
}
