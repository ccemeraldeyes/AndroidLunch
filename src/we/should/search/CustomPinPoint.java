package we.should.search;

import java.util.ArrayList;
import java.util.List;

import we.should.CustomDialog;
import we.should.list.Item;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * CustomPinPoint is a class that help you add Image onto the Google Map.
 * will be useful when we add the pin later.
 * @author Lawrence
 */
public class CustomPinPoint extends ItemizedOverlay<OverlayItem>{
	private List<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Item item;
	private Context context;
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
	public CustomPinPoint(Drawable m, Context context, Item item) {
		this(m);
		this.context = context;
		this.item = item;
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
//		This can be used to find the distance.		
//		float[] results = new float[1];
//		Location.distanceBetween(myLoc.getLatitudeE6(),myLoc.getLongitudeE6()
//				,placeLocation.getLatitudeE6(), placeLocation.getLongitudeE6()
//				,results);
		Dialog dialog = new CustomDialog(context, item, 0, 0);
		dialog.show();
		return true;
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
