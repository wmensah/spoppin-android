package net.wilmens.spoppin;

public interface IGPSActivity {
    public void locationChanged(double longitude, double latitude);
    public void gpsDisabled();
    public void gpsEnabled();
}