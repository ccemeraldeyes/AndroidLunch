package we.should;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Field;
import we.should.list.GenericCategory;
import we.should.list.Item;
import we.should.list.Movies;
import we.should.search.CustomPinPoint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	
	private final Category RESTAURANTS = new GenericCategory("Restaurants", Field.getDefaultFields(), this);
	private final Category MOVIES = new Movies(this);
	private final Category REFERRALS = new GenericCategory("Referrals", new ArrayList<Field>(), this);
	
	private static final List<CustomPinPoint> lstPinPoints = new ArrayList<CustomPinPoint>();
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	
	/** A map that maps the name of each category to its in-memory representation. **/
	private Map<String, Category> mCategories;
	
	/** A mapping from id's to categorys, used for submenu creation. **/
	private Map<Integer, Category> mMenuIDs;
	private MapView map;
	private LocationManager lm;
	private MapController controller;
	private String towers;
	private int devX, devY;
	private MyLocationOverlay myLocationOverlay;
	private List<Overlay> overlayList;
	protected WSdb db;
	protected String DBFILE;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);       
        controller = map.getController();
        overlayList = map.getOverlays();
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        updateTabs();        
        this.mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				updatePins(tabId.trim());
			}
		});
        db = new WSdb(this);
        //Testing
        myLocationOverlay = new MyLocationOverlay(this, map);
        map.getOverlays().add(myLocationOverlay);
        //Getting current location.
        Location location = getDeviceLocation();
        if(location != null) {
        	zoomLocation(location);
        } else {
        	Toast.makeText(WeShouldActivity.this, 
        			"Couldn't get provider", Toast.LENGTH_SHORT).show();
        }
        map.postInvalidate();    
    }

    protected void updatePins(String name) {
    	//clear the pin everytime we load a new tab.
    	for(CustomPinPoint pin : lstPinPoints) {
    		overlayList.remove(pin);
    	}
    	
    	List<Item> items = mCategories.get(name).getItems();
		Drawable customPin = getResources().getDrawable(R.drawable.google_place); //default for now.
    	for (Item item : items) {
    		Set<Address> addrs = item.getAddresses();
    		for(Address addr : addrs) {
    			try {
    				int locX = (int) (addr.getLatitude() * 1E6);
    				int locY = (int) (addr.getLongitude() * 1E6);
        			GeoPoint placeLocation = new GeoPoint(locX, locY);
        			OverlayItem overlayItem = new OverlayItem(placeLocation, item.getName(), item.get(Field.ADDRESS));
        			CustomPinPoint custom = new CustomPinPoint(customPin, WeShouldActivity.this);
        			custom.insertPinpoint(overlayItem);
        			lstPinPoints.add(custom);
        			overlayList.add(custom);
    			} catch (IllegalStateException ex) {
    				Log.v("UPDATEMAPVIEW", item.getName() + "'s address doesn't have lat or lng value");
    			}

    		}
    	}
	}

	/**
     * Updates the tabs on startup or when categories change.
     */
	private void updateTabs() {
		mCategories = new HashMap<String, Category>();
		Set<Category> categories = Category.getCategories(this);
		for (Category cat : categories) {
			mCategories.put(cat.getName(), cat);
		}
		if (mCategories.size() == 0) {
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
        TabPopulator tp = new TabPopulator();
        for (String name : mCategories.keySet()) {
	        spec = mTabHost.newTabSpec(name).setIndicator("  " + name + "  ")
	        		.setContent(tp);
	        mTabHost.addTab(spec);
        }
        updatePins(mTabHost.getCurrentTabTag().trim());
	}

	@Override
	protected void onPause() {
		//TODO: remember to stop all asynTask before exit!!!!
		super.onPause();
		myLocationOverlay.disableMyLocation();
		lm.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		lm.requestLocationUpdates(towers, 500, 1, this);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
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
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			intent.putExtra(HELP_TEXT, R.string.help_home);
			startActivity(intent);
			break;
		case R.id.add_cat:
			intent = new Intent(this, NewCategory.class);
			startActivityForResult(intent, ActivityKey.NEW_CAT.ordinal());
			break;
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateTabs();
	}

	public void onLocationChanged(Location location) {
		devX = (int) (location.getLatitude() * 1E6);
		devY = (int) (location.getLongitude() * 1E6);
		GeoPoint ourLocation = new GeoPoint(devX, devY);
		controller.animateTo(ourLocation);
	}
	
	//We will handle this later.
	public void onProviderDisabled(String provider) {		
	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private class TabPopulator implements TabContentFactory {

		public View createTabContent(String tag) {
			ListView lv = new ListView(getApplicationContext());
			
			String cleanedTag = tag.trim(); // because we use spaces for formatting
			Category cat = mCategories.get(cleanedTag);
			if (cat == null) {
				throw new IllegalStateException("Category not found!?");
			}
			
			final List<Item> itemsList = cat.getItems();
			lv.setAdapter(new ItemAdapter(WeShouldActivity.this, itemsList));
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	Item item = itemsList.get(position);
					Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
					intent.putExtra(CATEGORY, item.getCategory().getName());
					intent.putExtra(INDEX, position);
					startActivityForResult(intent, ActivityKey.VIEW_ITEM.ordinal());
			    }
			  });
			
			return lv;
		}
		
	}
	
	/*
	 * @return the location of the device
	 */
	private Location getDeviceLocation() {
		 lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // it is a string
	     Criteria crit = new Criteria();
	     towers = (lm.getBestProvider(crit, false)); //getting best provider.
	     return lm.getLastKnownLocation(towers);	
	}
	
	/*
	 * if location is valid, make a pint and zoom user to that location.
	 * if the location isn't valid, it display error message with toast. 
	 */
	private void zoomLocation(Location location) {
	    devX = (int) (location.getLatitude() * 1E6);
	    devY = (int) (location.getLongitude() * 1E6);
	    GeoPoint ourLocation = new GeoPoint(devX, devY);
	    controller.animateTo(ourLocation);
	    controller.setZoom(18);
	}
}