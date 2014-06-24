package net.wilmens.spoppin.utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
	
	public static Dialog CreateDialog(String message
			, String positiveButtonText
			, DialogInterface.OnClickListener onClickListener
			, Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
               .setPositiveButton(positiveButtonText, onClickListener);

		return builder.create();		
	}
}
