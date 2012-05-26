package we.should;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import we.should.communication.RestoreService;
import we.should.list.Category;
import we.should.list.Field;
import we.should.list.GenericCategory;
import we.should.list.Item;
import we.should.list.Movies;
import we.should.list.Referrals;
import we.should.list.Tag;
import we.should.search.CustomPinPoint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class WeShouldActivity extends MapActivity implements LocationListener {
	
	/** Preference keys. **/
	public static final String PREFS = "we.should.PREFS";
	public static final String ACCOUNT_NAME = "we.should.ACCOUNT_NAME";
	
	/** Bundle keys. **/
	public static final String CATEGORY = "CATEGORY";
	public static final String INDEX = "INDEX";
	public static final String HELP_TEXT = "HELP_TEXT";
	public static final String TAGS = "TAGS";
	
	private final Category RESTAURANTS = new GenericCategory(Category.Special.Restaurants.toString(), Field.getDefaultFields(), this);
	private final Category MOVIES = new Movies(this);
	private final Category REFERRALS = new Referrals(this);

	/** for Map **/
	public static double DISTANCETOMILES =  0.000621371192;
	private static final List<CustomPinPoint> lstPinPoints = new ArrayList<CustomPinPoint>();
	
	/** The TabHost that cycles between tabs. **/
	private TabHost mTabHost;
	
	/** The adapter in the view of the current tab. **/
	private ItemAdapter mAdapter;
	
	/** How we want to sort said tabs. **/
	private SortType mSortType;
	
	/** A map that maps the name of each category to its in-memory representation. **/
	private Map<String, Category> mCategories;
	
	/** A map that maps the name of each tag to its values. **/
	private Map<String, Tag> mTags;
	
	/** A global reference to the selected Item **/
	private Item mItem;
	
	/** A mapping from id's to categories, used for submenu creation. **/
	private Map<Integer, Category> mMenuIDs;
	private MapView map;
	private LocationManager lm;
	private MapController controller;
	private String towers;
	private MyLocationOverlay myLocationOverlay;
	private List<Overlay> overlayList;
	private ImageButton zoomButton;
	
	//protected WSdb db;
	protected String DBFILE;
	
	/** An enum describing how we want to group our tabs. **/
	private static enum SortType {
		Category, Tag;
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        map = (MapView) findViewById(R.id.mapview);       
        controller = map.getController();
        overlayList = map.getOverlays();
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.zoomButton = (ImageButton) findViewById(R.id.my_location_button);
        mSortType = SortType.Category;
        updateTabs();        
        this.mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				updateView(tabId.trim());
			}
		});
        
        this.zoomButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				GeoPoint loc = getDeviceLocation();
				if(loc != null) {
					zoomLocation(loc);
				}
			}
        });
        
        myLocationOverlay = new MyLocationOverlay(this, map);
        map.getOverlays().add(myLocationOverlay);
        //Getting current location.
        GeoPoint location = getDeviceLocation();
        if(location != null) {
        	zoomLocation(location);
        }
        map.postInvalidate();    
    }
    
    /**
     * updating the view, updating the item adapter as well as all the pins.
     * @param name - the name of the tag or category.
     */
    protected void updateView(String name) {
    	
    	List<Item> items = null;
    	
    	Color color = Color.Red;
		
    	switch (mSortType) {
    	case Category:
    		items = mCategories.get(name).getItems();
    		color = mCategories.get(name).getColor();
    		break;
    	case Tag:
    		items = new ArrayList<Item>(Item.getItemsOfTag(mTags.get(name), this));
    		color = mTags.get(name).getColor();
    		break;
    	}
    	mAdapter = new ItemAdapter(WeShouldActivity.this, items);
    	mAdapter.notifyDataSetChanged();
    	updatePins(color, items);
	}
    
    /**
     * This method remove all the pin in current map 
     * and create a list of pins base on the item it is given.
     * 
     * @param color - a color string, 6 character long represent r, g, b
     * @param items - the items that needed to create the pin for.
     */
    private void updatePins(Color color, List<Item> items){
    	//clear the pin everytime we load a new tab.
    	for(CustomPinPoint pin : lstPinPoints) {
    		overlayList.remove(pin);
    	}
    	if(color == null || items == null) {
    		throw new RuntimeException("fail to get item from category or tags");
    	}
    	
    	for (Item item : items) {
    		Set<Address> addrs = item.getAddresses();
    		for(Address addr : addrs) {
				if(addr.hasLatitude() && addr.hasLongitude()) {
    				int locX = (int) (addr.getLatitude() * 1E6);
    				int locY = (int) (addr.getLongitude() * 1E6);
        			GeoPoint placeLocation = new GeoPoint(locX, locY);
        			addPin(placeLocation, color, item, false);
				}
    		}
    	}
    	
    }
    
    /**
     * Updates the tabs depending on what we want to sort by.
     */
    private void updateTabs() {
    	String tabId = mTabHost.getCurrentTabTag();
    	switch (mSortType) {
    	case Category:
    		updateTabsCategory();
    		break;
    	case Tag:
    		updateTabsTag();
    		break;
    	}
    	mTabHost.setCurrentTabByTag(tabId);
    }

	/**
     * Updates the tabs on startup or when categories change.
     */
	private void updateTabsCategory() {
		mCategories = new HashMap<String, Category>();
		Set<Category> categories = Category.getCategories(this);
		for (Category cat : categories) {
			mCategories.put(cat.getName(), cat);
		}
		if (mCategories.size() == 0) {
			//Initialize DB if first app launch.
			mCategories.put(MOVIES.getName(), MOVIES);
			mCategories.put(RESTAURANTS.getName(), RESTAURANTS);
			mCategories.put(REFERRALS.getName(), REFERRALS);
			MOVIES.save();
			RESTAURANTS.save();
			REFERRALS.save();
		}

        mTabHost.setup();
		mTabHost.clearAllTabs();
		TabHost.TabSpec spec;
        CategoryPopulator tp = new CategoryPopulator();
        for (String name : mCategories.keySet()) {
	        spec = mTabHost.newTabSpec(name).setIndicator("  " + name + "  ")
	        		.setContent(tp);
	        mTabHost.addTab(spec);
        }
	}
	
	/**
	 * Updates the tabs when tags change.
	 */
	private void updateTabsTag() {
		mTags = new HashMap<String, Tag>();
		List<Tag> tags = Tag.getTags(this);
		for (Tag tag : tags) {
			mTags.put(tag.toString(), tag);
		}
		mTabHost.setup();
		mTabHost.clearAllTabs();
		TabHost.TabSpec spec;
		TagPopulator tp = new TagPopulator();
		for (String name : mTags.keySet()) {
			spec = mTabHost.newTabSpec(name).setIndicator("  " + name + "  ")
					.setContent(tp);
			mTabHost.addTab(spec);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
		lm.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		lm.requestLocationUpdates(towers, 500, 1, this);
		updateView(mTabHost.getCurrentTabTag());
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		// Set up the toggle item
		MenuItem toggle = menu.findItem(R.id.toggle);
		switch (mSortType) {
		case Category:
			toggle.setTitle("Sort by tags");
			break;
		case Tag:
			toggle.setTitle("Sort by categories");
			break;
		}
		
		// Now populate the custom submenu
		SubMenu addMenu = menu.findItem(R.id.add_item).getSubMenu();
		mMenuIDs = new HashMap<Integer, Category>();
		int i = Menu.FIRST;
		for (String s : mCategories.keySet()) {
			Category cat = mCategories.get(s);
			if (!cat.getName().equals(REFERRALS.getName())) {
				addMenu.add(R.id.add_item, i, i, s);
				mMenuIDs.put(i, cat);
				i++;
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.restore:
			SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
			Intent service = new Intent(this, RestoreService.class);
			service.putExtra(WeShouldActivity.ACCOUNT_NAME, settings.getString(WeShouldActivity.ACCOUNT_NAME, ""));
			startService(service);
			break;
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			intent.putExtra(HELP_TEXT, R.string.help_home);
			startActivity(intent);
			break;
		case R.id.add_cat:
			intent = new Intent(this, NewCategory.class);
			startActivityForResult(intent, ActivityKey.NEW_CAT.ordinal());
			break;
		case R.id.toggle:
			switch (mSortType) {
			case Category:
				if (Tag.getTags(this).size() > 0) {
					mSortType = SortType.Tag;
				} else {
					Toast.makeText(this, "There are currently no tags", Toast.LENGTH_LONG).show();
				}
				break;
			case Tag:
				mSortType = SortType.Category;
				break;
			}
			updateTabs();
		}
		
		// It's probably in the submenu
		Category cat = mMenuIDs.get(item.getItemId());
		if (cat != null) {
			Intent intent = new Intent(this, EditScreen.class);
			intent.putExtra(CATEGORY, cat.getName());
			intent.putExtra(INDEX, -1);
			startActivityForResult(intent, ActivityKey.NEW_ITEM.ordinal());
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem.getMenuInfo();
		mItem = mAdapter.getItem(info.position);
		switch(menuItem.getItemId()) {
		case R.id.view:
			Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
			intent.putExtra(CATEGORY, mItem.getCategory().getName());
			intent.putExtra(INDEX, mItem.getId());
			startActivityForResult(intent, ActivityKey.VIEW_ITEM.ordinal());
			break;
		case R.id.edit:
			intent = new Intent(getApplicationContext(), EditScreen.class);
			intent.putExtra(CATEGORY, mItem.getCategory().getName());
			intent.putExtra(INDEX, mItem.getId());
			startActivityForResult(intent, ActivityKey.EDIT_ITEM.ordinal());
			break;
		case R.id.delete:
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.delete_item)
	        .setMessage(R.string.delete_item_confirm)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        	
	        	public void onClick(DialogInterface dialog, int which) {
	    			mAdapter.remove(mItem);
	                mItem.delete();
	                updateTabs();
	            }

	        })
	        .setNegativeButton(R.string.no, null)
	        .show();
			break;
		default:
			return super.onContextItemSelected(menuItem);
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateTabs();
	}
	
	//Do nothing when user change the location of the map
	public void onLocationChanged(Location location) {}
	
	//Do nothing when the provider of the location listener(GPS or internet)
	//disable or enable or statuschanged.
	public void onProviderDisabled(String provider) {}
	public void onProviderEnabled(String provider) {}
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * This class populates each tab with items from that tab's category.
	 * 
	 * @author Will
	 */
	private class CategoryPopulator implements TabContentFactory {

		public View createTabContent(String tag) {
			ListView lv = new ListView(getApplicationContext());
			
			String cleanedTag = tag.trim(); // because we use spaces for formatting
			Category cat = mCategories.get(cleanedTag);
			if (cat == null) {
				throw new IllegalStateException("Category not found!?");
			}
			
			final List<Item> itemsList = cat.getItems();
			mAdapter = new ItemAdapter(WeShouldActivity.this, itemsList);
			lv.setAdapter(mAdapter);
			registerForContextMenu(lv);
			
			// click to view item & current location in map
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {				
			    	Item item = itemsList.get(position);
			    	Set<Address> addrs = item.getAddresses();			    
			    	for(Address addr : addrs) {
			    		if(addr.hasLatitude() && addr.hasLongitude()) {
					    	int locX = (int) (addr.getLatitude() * 1E6);
		    				int locY = (int) (addr.getLongitude() * 1E6);
		        			GeoPoint placeLocation = new GeoPoint(locX, locY);
		        			GeoPoint myLoc = getDeviceLocation();
		        			//Loop through the pins, remove the normal pin and add yellow pin.
		        			//and replace any yellow pin back to normal pin.      			
		        			if(myLoc == null) {
		        				zoomLocation(placeLocation);
		        			} else {
		        				zoomToTwoPoint(placeLocation, myLoc);
		        			}	        			
		        			updateYellowPin(placeLocation);
		        		    break;
			    		}
				    }
			    }
			});
			return lv;
		}
	}
	
	
	/**
	 * updating the yellowPin when item is clicked.
	 * @param placeLocation
	 */
	private void updateYellowPin(GeoPoint placeLocation) {
		CustomPinPoint replaceToColorPin = null;
		CustomPinPoint replaceToYellowPin = null;
		for(CustomPinPoint customPin : lstPinPoints) {
			if(customPin.contains(placeLocation)) {
				replaceToYellowPin = customPin;
			}
			if(customPin.isSelected()) {
				replaceToColorPin = customPin;
			}
			if(replaceToColorPin != null && replaceToYellowPin != null) {
				break;
			}
		}
		
		if(replaceToColorPin != null) {
			overlayList.remove(replaceToColorPin);
			lstPinPoints.remove(replaceToColorPin);
			addPin(replaceToColorPin.getPoint(), 
					replaceToColorPin.getColor(), 
					replaceToColorPin.getItem(), false);
		}
		
		if(replaceToYellowPin != null) {
			overlayList.remove(replaceToYellowPin);
			lstPinPoints.remove(replaceToYellowPin);
			addPin(replaceToYellowPin.getPoint(),
					replaceToYellowPin.getColor(),
					replaceToYellowPin.getItem(), true);
		}
	}
	
	/**
	 * This class populates each tab with items from that tab's tag.
	 * 
	 * @author Will
	 */
	private class TagPopulator implements TabContentFactory {
		
		public View createTabContent(String name) {
			Context ctx = getApplicationContext();
			ListView lv = new ListView(ctx);
			
			String cleanedTag = name.trim(); // because we use spaces for formatting
			Tag tag = Tag.get(ctx, cleanedTag);
			if (tag == null) {
				throw new IllegalStateException("Tag not found!?");
			}
			
			final List<Item> itemsList = new ArrayList<Item>(Item.getItemsOfTag(tag, ctx));
			mAdapter = new ItemAdapter(WeShouldActivity.this, itemsList);
			lv.setAdapter(mAdapter);
			registerForContextMenu(lv);
			
			// click to view item & current location in map
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					
			    	Item item = itemsList.get(position);
			    	Set<Address> addrs = item.getAddresses();
			    
			    	for(Address addr : addrs) {
			    		if(addr.hasLatitude() && addr.hasLongitude()) {
					    	int locX = (int) (addr.getLatitude() * 1E6);
		    				int locY = (int) (addr.getLongitude() * 1E6);
		        			GeoPoint placeLocation = new GeoPoint(locX, locY);
		        			GeoPoint myLoc = getDeviceLocation();
		        			if(myLoc == null) {
		        				zoomLocation(placeLocation);
		        			} else {
		        				zoomToTwoPoint(placeLocation, myLoc);
		        			}
		        			updateYellowPin(placeLocation);
		        		    break;
			    		}
				    }
			    }
			});
			
			return lv;
		}
	}
	
	/**
	 * helper method to add a pin to the map.
	 * @param point - the point that you want to add the pin
	 * @param color - 6 character color string, with r, g, b represent the color of the pin
	 * @param item - the item the pin representing
	 * @param isSelected - true, if user is current viewing the pin
	 * 			- false otherwise.
	 * @throws IllegalArgumentException on null input.
	 */
	private void addPin(GeoPoint point, Color color, Item item, boolean isSelected) {
		if(point == null || color == null || item == null) {
			throw new IllegalArgumentException("input to addPin is null");
		}
		
		double distance = distanceBetween(getDeviceLocation(), point);
		Drawable drawable;
		if(isSelected) {
			drawable = getDrawable(Color.Yellow);
		} else {
			drawable = getDrawable(color);
		}
		
		CustomPinPoint custom = new CustomPinPoint(drawable, this, item, distance, isSelected, color);
		OverlayItem overlay = new OverlayItem(point, item.getName(), item.getName());
		custom.addOverlay(overlay);
		lstPinPoints.add(custom);
		overlayList.add(custom);
	}
	
	/**
	 * @return the location of the device
	 */
	private GeoPoint getDeviceLocation() {
		 lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // it is a string
	     Criteria crit = new Criteria();
	     towers = (lm.getBestProvider(crit, false)); //getting best provider.
	     Location location = lm.getLastKnownLocation(towers);
	     if(location == null) {
	        Toast.makeText(WeShouldActivity.this, 
	        			"Couldn't get providers", Toast.LENGTH_SHORT).show();
	    	 return null;
	     } else {
		     int myLocX = (int) (location.getLatitude() * 1E6);
			 int myLocY = (int) (location.getLongitude() * 1E6);
		     return new GeoPoint(myLocX, myLocY);
		}
	}
	
	/**
	 * zoom to a view that captures two points.
	 * google support zoomToSpan, try their best to fit two points in the same view.
	 * 
	 * @param point - point 1 to be include in the view
	 * @param point2 - point2 to be include in the view
	 * 	 
	 * @throws IllegalArgumentException when the point is null
	 */
	private void zoomToTwoPoint(GeoPoint point, GeoPoint point2) {
		if(point == null || point2 == null) {
			throw new IllegalArgumentException("GeoPoint are null");
		}
		int maxX = Math.max(point.getLatitudeE6(), point2.getLatitudeE6());
		int minX = Math.min(point.getLatitudeE6(), point2.getLatitudeE6());
		int maxY = Math.max(point.getLongitudeE6(), point2.getLongitudeE6());
		int minY = Math.min(point.getLongitudeE6(), point2.getLongitudeE6());
		controller.zoomToSpan(maxX - minX, maxY - minY);
		controller.animateTo(new GeoPoint((minX + maxX) / 2, (minY + maxY) / 2));
	}
	
	/**
	 * if location is valid, make a point and zoom user to that location.
	 * if the location isn't valid, it display error message with toast. 
	 * @param location
	 */
	private void zoomLocation(GeoPoint location) {
	    controller.animateTo(location);
	    controller.setZoom(17);
	}
	
	
	/**
	 * @param p1 - point1
	 * @param p2 - point2
	 * @return the distance between the two points in miles.
	 * @throws IllegalArgumentException when the point is null
	 */
	private double distanceBetween(GeoPoint p1, GeoPoint p2) {
		if(p1 == null || p2 == null) {
			throw new IllegalArgumentException("GeoPoint are null");
		}
		Location loc1 = new Location("");
		Location loc2 = new Location("");
		loc1.setLatitude(p1.getLatitudeE6() / 1E6);
		loc1.setLongitude(p1.getLongitudeE6() / 1E6);
		loc2.setLatitude(p2.getLatitudeE6() / 1E6);
		loc2.setLongitude(p2.getLongitudeE6() / 1E6);
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(loc1.distanceTo(loc2) * DISTANCETOMILES));
	}
	
	
	/**
	 * Getting the drawable base by the color string.
	 * @param color - string with six characters format, represent r, g, b
	 * @return Drawable the pin represent for that color.
	 */
	private Drawable getDrawable(Color color) {
		if(color == null) { // || color.length() != 6) {
			throw new IllegalArgumentException("color is null or " +
					"color string is suppose to be six characters");
		}
		switch(color){
		case Yellow:
			return getResources().getDrawable(R.drawable.yellow);
		case Green:
			return getResources().getDrawable(R.drawable.green);
		case Blue:
			return getResources().getDrawable(R.drawable.blue);
		case Purple:
			return getResources().getDrawable(R.drawable.purple);
		case Red:
			return getResources().getDrawable(R.drawable.red);
		default:
			return getResources().getDrawable(R.drawable.red);
		}
			
//		if(color.equals("FFFF00")) {//TODO: suppose to be hex , just for the special case of highlighting.
//			return getResources().getDrawable(R.drawable.yellow);
//		} else if(color.equals("00FF00")) {
//			return getResources().getDrawable(R.drawable.green);
//		} else if(color.equals("0000FF")) {
//			return getResources().getDrawable(R.drawable.blue);
//		} else if(color.equals("FF00FF")) {
//			return getResources().getDrawable(R.drawable.purple);
//		} else {
//			return getResources().getDrawable(R.drawable.red);
//		}
	}
	
}