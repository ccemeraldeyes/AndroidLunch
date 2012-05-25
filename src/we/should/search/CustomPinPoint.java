package we.should.search;
import java.util.ArrayList;
import java.util.List;
import we.should.CustomDialog;
import we.should.list.Item;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * A CustomPinPoint on the map.
 * @author Lawrence
 */
public class CustomPinPoint extends ItemizedOverlay<OverlayItem> {
	private Item item;
	private Context context;
	private List<OverlayItem> mapOverlays;
	private double distance;
	private boolean isSelected;
	private String color;
	
	//Here if the color indicate the color of the pin, but if select is true, the pin is yellow color.
	//It is useful when we need to revert back to the original color.
	public CustomPinPoint(Drawable pin, Context context, Item item, double distance, boolean isSelected, String color) {
		super(boundCenterBottom(pin));
		this.context = context;
		this.item = item;
		this.distance = distance;
		mapOverlays = new ArrayList<OverlayItem>();
		this.isSelected = isSelected;
		this.color = color;
	}
	
	/**
	 * this method is call when user click on the item
	 */
	@Override
	protected boolean onTap(int index) {
		Dialog popup = new CustomDialog(context, item, this.distance, 0);
		popup.show();
		return true;
	}
	
	/**
	 * getting the overlayItem given an index
	 */
	@Override
	protected OverlayItem createItem(int index) {
		if(index < 0 || index >= mapOverlays.size()) {
			throw new IllegalArgumentException("index out of bound when getting OverlayItem");
		}
		return mapOverlays.get(index);
	}

	/**
	 * @param overlay - add the overlay
	 */
	public void addOverlay(OverlayItem overlay) {
		if(overlay == null) {
			throw new IllegalArgumentException("overlay is null");
		}
		mapOverlays.add(overlay);
		populate();
	}
	
	/**
	 * Remove the overlay item.
	 * @param overlay
	 */
	public void removeOverlay(int index) {
		if(index < 0 || index >= mapOverlays.size()) {
			throw new IllegalArgumentException("index out of bound when getting OverlayItem");
		}
		mapOverlays.remove(index);
	}
	
	/**
	 * return the size of the mapOverlays.
	 */
	@Override
	public int size() {
		return mapOverlays.size();
	}
	
	/**
	 * @param p - to see if the CustomPinPoints contains that point
	 * @return true if it contains, false otherwise.
	 */
	public boolean contains(GeoPoint p) {
		for(OverlayItem overlay : mapOverlays) {
			if(overlay.getPoint().equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return String - the original color of the pin, yellow pin may return red,
	 *  because yellow is the special pin, allow us to make a pin with the original color.
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * @return true if the pin is yellow, the user selecting it, false otherwise.
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * @return getting the GeoPoint, so that we can build a pin in that location.
	 */
	public GeoPoint getPoint() {
		for(OverlayItem overlay : mapOverlays) {
			return overlay.getPoint();
		}
		return null;
	}
	
	/**
	 * @return Item, the item in the pinpoint, useful for rebuilding a new pin with different drawable.
	 */
	public Item getItem() {
		return item;
	}
}
