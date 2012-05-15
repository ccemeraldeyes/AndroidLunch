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
public class CustomPinPoint extends ItemizedOverlay{
	private List<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Context c;
	
	public CustomPinPoint(Drawable d) {
		//drawing the Drawable to the map
		super(boundCenter(d));
	}

	public CustomPinPoint(Drawable m, Context context) {
		this(m);
		c = context;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return pinpoints.get(i);
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem overlay = pinpoints.get(index);
		
		
		return super.onTap(index);
	}

	@Override
	public int size() {
		return pinpoints.size();
	}
	
	public void insertPinpoint(OverlayItem item) {
		pinpoints.add(item);
		this.populate();
	}
}
