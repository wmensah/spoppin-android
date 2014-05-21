package net.wilmens.spoppin.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VenueScoreInfo {
	private double drinks;
	public double getDrinks(){
		return drinks;
	}
	public void setDrinks(double value){
		drinks = value;
	}
	
	private double music;
	public double getMusic(){
		return music;
	}
	public void setMusic(double value){
		music = value;
	}
	
	private double girls;
	public double getGirls(){
		return girls;
	}
	public void setGirls(double value){
		girls = value;
	}
	
	private double guys;
	public double getGuys(){
		return guys;
	}
	public void setGuys(double value){
		guys = value;
	}
	
	public ScoreCategoryEnum getBestCategory(){
		Map<ScoreCategoryEnum, Double> data = new HashMap<ScoreCategoryEnum, Double>();
		data.put(ScoreCategoryEnum.Drinks, drinks);
		data.put(ScoreCategoryEnum.Music, music);
		data.put(ScoreCategoryEnum.Girls, girls);
		data.put(ScoreCategoryEnum.Guys, guys);
		
		Entry<ScoreCategoryEnum, Double> maxEntry = null;
		
		for(Entry<ScoreCategoryEnum, Double> entry : data.entrySet()){
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()){
				maxEntry = entry;
			}
		}
		
		return maxEntry.getKey();
	}
}
