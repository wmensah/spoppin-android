package com.example.spoppin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BarRankAdapter extends ArrayAdapter<Object> {
	Context context;
	int layoutResourceId;
	BarRank data[] = null;
	
	public BarRankAdapter(Context context, int layoutResourceId, BarRank[] data){
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;	
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
			holder.lblSpops = (TextView)row.findViewById(R.id.lblSpops);
			holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
			
			row.setTag(holder);			
		}else{
			holder = (BarRankHolder)row.getTag();
		}
		
		BarRank barRank = data[position];
		holder.lblName.setText(barRank.name);
		holder.lblRank.setText(Integer.toString(barRank.rank));
		holder.lblSpops.setText(Integer.toString(barRank.spops));
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
		TextView lblSpops;
	}
	
}
