package we.should;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public enum PinColor{
	//Yellow(R.drawable.yellow),
	Green(R.drawable.green),
	Blue(R.drawable.blue),
	Purple(R.drawable.purple),
	Red(R.drawable.red),
	White(R.drawable.white),
	Pink(R.drawable.pink),
	Brown(R.drawable.brown);
	
	private int mDrawable;
	
	/**
	 * @return the int ID of the drawable representing this pin
	 */
	public int getDrawable() {
		return mDrawable;
	}
	
	private PinColor(int drawable) {
		mDrawable = drawable;
	}
	
	public static final PinColor DEFAULT = Red;
	/**
	 * Returns the color enum constant associated with the color string
	 * @param color
	 * @return the Color enum associated with color or Color.DEFAULT if it doesn't exist.
	 */
	public static PinColor get(String color) {
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
		PinColor[] colors = PinColor.values();
		List<String> out = new ArrayList<String>();
		for(PinColor c : colors) out.add(c.name());
		return out;
	}
}
