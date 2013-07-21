package com.example.spoppin;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
			
			row.setTag(holder);			
		}else{
			holder = (BarRankHolder)row.getTag();
		}
		
		BarRank barRank = data.get(position);
		holder.lblName.setText(barRank.name);
		holder.lblRank.setText(Integer.toString(barRank.rank));
		holder.lblDrinks.setText("drinks:" + Double.toString(barRank.score.getDrinks()));
		holder.lblMusic.setText("music:" + Double.toString(barRank.score.getMusic()));
		holder.lblGirls.setText("girls:" + Double.toString(barRank.score.getGirls()));
		holder.lblGuys.setText("guys:" + Double.toString(barRank.score.getGuys()));
		holder.imgIcon.setImageResource(barRank.icon);
		
		if (barRank.rank > 1){
			holder.imgIcon.setVisibility(0);
		}
		
		return row;		
	}

	static class BarRankHolder{
		ImageView imgIcon;
		TextView lblRank;
		TextView lblName;
		TextView lblDrinks;
		TextView lblMusic;
		TextView lblGirls;
		TextView lblGuys;
	}
	
}
