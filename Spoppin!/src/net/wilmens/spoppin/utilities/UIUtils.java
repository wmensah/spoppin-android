package net.wilmens.spoppin.utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.wilmens.spoppin.R;
import net.wilmens.spoppin.objects.VenueColor;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class UIUtils {
	public static void Toast(Activity activity, String msg){
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();		
	}
	
	@SuppressLint("NewApi")
	public static Address GeocodeCoordinates(Activity activity, double lat, double lng, int maxResults) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()){
        	Geocoder gcd = new Geocoder(activity, Locale.getDefault());
	        List<Address> addresses = null;
	        try {
	            addresses = gcd.getFromLocation(lat, lng, maxResults);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
	        if (addresses != null && addresses.size() > 0)
	        	return addresses.get(0);
        }
        return null;

    }
	
	public static Dialog CreateDialog(String message
			, String positiveButtonText
			, String negativeButtonText
			, DialogInterface.OnClickListener positiveOnClickListener
			, DialogInterface.OnClickListener negativeOnClickListener
			, Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
               .setPositiveButton(positiveButtonText, positiveOnClickListener)
               .setNegativeButton(negativeButtonText, negativeOnClickListener);

		return builder.create();		
	}
	
	@SuppressLint("NewApi")
	public static Dialog CreateDialog(int message
			, int title
			, int positiveButtonText
			, DialogInterface.OnClickListener onClickListener
			, Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
        	.setIconAttribute(android.R.attr.alertDialogIcon)
        	.setTitle(title)
               .setPositiveButton(positiveButtonText, onClickListener);

		return builder.create();		
	}
	
	public static Dialog CreateDialog(int message
			, int positiveButtonText
			, DialogInterface.OnClickListener onClickListener
			, Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
        	.setIconAttribute(android.R.attr.alertDialogIcon)
               .setPositiveButton(positiveButtonText, onClickListener);

		return builder.create();		
	}
	
	/*
	 * Dialog with message, positive button text, and OK event handler
	 */
	public static void showAlertDialog(Context context, int message, int positiveButtonText, DialogInterface.OnClickListener onClickListener){
		Dialog dialog = CreateDialog(message
				, positiveButtonText
				, onClickListener
				, context);
		dialog.show();
	}
	
	/*
	 * Dialog with message and OK event handler
	 */
	public static void showAlertDialog(Context context, int message, DialogInterface.OnClickListener onClickListener){
		UIUtils.showAlertDialog(context, message, R.string.ok, onClickListener);
	}
	
	/*
	 * Dialog with message. OK button closes the dialog
	 */
	public static void showAlertDialog(Context context, int message){
		UIUtils.showAlertDialog(context, message, R.string.ok, 
				new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();						
			}				
		});
	}
			 
	public static Dialog CreateDialog(String message
			, int positiveButtonText
			, DialogInterface.OnClickListener onClickListener
			, Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
               .setPositiveButton(positiveButtonText, onClickListener);

		return builder.create();		
	}
	
	public static VenueColor[] getColorArray(){
		VenueColor[] colors = new VenueColor[10];
		colors[0] = new VenueColor("#FA58F4", BitmapDescriptorFactory.HUE_MAGENTA);
		colors[1] = new VenueColor("#01DF01", BitmapDescriptorFactory.HUE_GREEN);
		colors[2] = new VenueColor("#9A2EFE", BitmapDescriptorFactory.HUE_VIOLET);
		colors[3] = new VenueColor("#FE9A2E", BitmapDescriptorFactory.HUE_ORANGE);
		colors[4] = new VenueColor("#FE2E2E", BitmapDescriptorFactory.HUE_RED);
		colors[5] = new VenueColor("#2E64FE", BitmapDescriptorFactory.HUE_BLUE);
		colors[6] = new VenueColor("#66CCFF", BitmapDescriptorFactory.HUE_AZURE);
		colors[7] = new VenueColor("#FA58AC", BitmapDescriptorFactory.HUE_ROSE);
		colors[8] = new VenueColor("#58D3F7", BitmapDescriptorFactory.HUE_CYAN);
		colors[9] = new VenueColor("#FFFF00", BitmapDescriptorFactory.HUE_YELLOW);
		return colors;
	}
}
