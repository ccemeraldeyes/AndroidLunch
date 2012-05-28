package we.should;

import android.util.Log;

public enum Color{
	Yellow(R.drawable.yellow),
	Green(R.drawable.green),
	Blue(R.drawable.blue),
	Purple(R.drawable.purple),
	Red(R.drawable.red);
	
	private int mDrawable;
	
	/**
	 * @return the int ID of the drawable representing this pin
	 */
	public int getDrawable() {
		return mDrawable;
	}
	
	private Color(int drawable) {
		mDrawable = drawable;
	}
	
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
