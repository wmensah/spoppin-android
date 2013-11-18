package net.wilmens.spoppin;

import net.wilmens.spoppin.objects.Venue;
import net.wilmens.spoppin.objects.VenueScoreInfo;

public class BarRank {
	public Venue venue;
	public int icon;
	public VenueScoreInfo score;
	public int rank;

	public BarRank(){
		super();
	}
	public BarRank(Venue venue, int icon, VenueScoreInfo score, int rank){
		super();
		this.venue = venue;
		this.icon = icon;
		this.score = score;
		this.rank = rank;
	}
}
