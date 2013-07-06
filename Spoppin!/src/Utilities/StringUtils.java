package Utilities;

public class StringUtils {

	public static Boolean isNullOrEmpty(String str){
		if (str == null || str.length() == 0)
			return true;
		return false;
	}
}
