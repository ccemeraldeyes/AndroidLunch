package we.should;

import android.util.Log;

public enum Color{
	Yellow, Green, Blue, Purple, Red;
	
	public static final Color DEFAULT = Red;
	public static Color get(String color) {
		try{
			return valueOf(color);
		} catch (IllegalArgumentException e){
			Log.i("GetColor", "Returning Default");
			return DEFAULT;
		}
	}
}
