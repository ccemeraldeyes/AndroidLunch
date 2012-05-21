package we.should.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * CustomPinPoint is a class that help you add Image onto the Google Map.
 * will be useful when we add the pin later.
 * @author Lawrence
 */
public class CustomPinPoint extends ItemizedOverlay<OverlayItem>{
	private List<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	//private Context c;
	/**
	 * Creating a pinpoint with drawable
	 */
	public CustomPinPoint(Drawable d) {
		super(boundCenter(d));
	}

	/**
	 * @param m - Drawable 
	 * @param context - context will be useful on onTap
	 */
	public CustomPinPoint(Drawable m, Context context) {
		this(m);
		//this.c = context;//@param context - context will be useful on onTap
	}
	
	/**
	 * Creating an OverlayItem.
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return pinpoints.get(i);
	}
	
	/**
	 * What happen when suer click on the pin.
	 */
	@Override
	protected boolean onTap(int index) { 
		//OverlayItem overlay = pinpoints.get(index);
		return super.onTap(index);
	}

	/**
	 * return the number of pinpoints.
	 */
	@Override
	public int size() {
		return pinpoints.size();
	}
	
	/**
	 * put the overlay as one of our pinpoint.
	 * @param item - the overLayItem
	 */
	public void insertPinpoint(OverlayItem item) {
		pinpoints.add(item);
		this.populate();
	}
}
