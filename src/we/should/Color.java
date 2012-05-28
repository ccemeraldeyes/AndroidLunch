package we.should;

import java.util.ArrayList;
import java.util.List;

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
	/**
	 * Returns a the list of names of the declared colors as seen at
	 * the top of this file.
	 * @return a list of color names
	 */
	public static List<String> getColors(){
		Color[] colors = Color.values();
		List<String> out = new ArrayList<String>();
		for(Color c : colors) out.add(c.name());
		return out;
	}
}
