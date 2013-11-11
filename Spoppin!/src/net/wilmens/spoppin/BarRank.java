package net.wilmens.spoppin;

import net.wilmens.spoppin.objects.VenueScoreInfo;

public class BarRank {
	public int venueId;
	public int icon;
	public VenueScoreInfo score;
	public int rank;
	public String name;
	public BarRank(){
		super();
	}
	public BarRank(int venueId, int icon, String name, VenueScoreInfo score, int rank){
		super();
		this.venueId = venueId;
		this.icon = icon;
		this.score = score;
		this.name = name;
		this.rank = rank;
	}
}
