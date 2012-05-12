package we.should;

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
import we.should.search.DetailPlace;
import we.should.search.Place;
import we.should.search.PlaceRequest;
import we.should.search.PlaceType;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

public class WeShouldActivity extends MapActivity implements LocationListener {
	
	/** Bundle keys. **/
	public static final String CATEGORY = "CATEGORY";
	public static final String INDEX = "INDEX";
	
	/** Activity keys. **/
	private static final int NEW_CAT = 0;
	private static final int NEW_ITEM = 1;
	
	private final Category RESTAURANTS = new GenericCategory("Restaurants", Field.getDefaultFields(), this);
	private final Category MOVIES = new Movies(this);

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
        this.mTabHost.setup();
        updateTabs();        

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
        			"Couldn''t get provider", Toast.LENGTH_SHORT).show();
        }
        map.postInvalidate();

        //Testing SearchLocation and SearchDetailPlace - Lawrence
//        SearchLocationSrv srv = new SearchLocationSrv(location, "university");
//		setProgressBarIndeterminateVisibility(true);
//		srv.execute();
//        
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
			MOVIES.save();
			RESTAURANTS.save();
		}
		
		mTabHost.clearAllTabs();
		TabHost.TabSpec spec;
        TabPopulator tp = new TabPopulator();
        for (String name : mCategories.keySet()) {
	        spec = mTabHost.newTabSpec(name).setIndicator("  " + name + "  ")
	        		.setContent(tp);
	        mTabHost.addTab(spec);
        }
        mTabHost.setCurrentTab(0);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		// Now populate the custom submenu
		SubMenu addMenu = menu.findItem(R.id.add_item).getSubMenu();
		mMenuIDs = new HashMap<Integer, Category>();
		int i = Menu.FIRST;
		for (String s : mCategories.keySet()) {
			addMenu.add(R.id.add_item, i, i, s);
			mMenuIDs.put(i, mCategories.get(s));
			i++;
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			startActivity(intent);
			break;
		case R.id.add_cat:
			intent = new Intent(this, NewCategory.class);
			startActivityForResult(intent, NEW_CAT);
			break;
		}
		
		// It's probably in the submenu
		Category cat = mMenuIDs.get(item.getItemId());
		if (cat != null) {
			Intent intent = new Intent(this, EditScreen.class);
			intent.putExtra(CATEGORY, cat.getName());
			intent.putExtra(INDEX, -1);
			startActivityForResult(intent, NEW_ITEM);
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
			lv.setAdapter(new ItemAdapter(WeShouldActivity.this, R.layout.item_row, itemsList));
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	Item item = itemsList.get(position);
					Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
					intent.putExtra(CATEGORY, item.getCategory().getName());
					intent.putExtra(INDEX, position);
					startActivity(intent);
			    }
			  });
			
			return lv;
		}
		
	}
	
	
	//
	//SearchLocationSrv is an AsyncTask that help you do the query by Name
	//According to Google Place API, it will return all place that contain the exact given Name
	//
	//Google Place API only allow you to search Places within 50000 meters (30 miles), so place
	//with name that is further than that will not be detected.
	//
	//This query will return a List of Place, but Place only have coordinate, if you want
	//further detail on one place, you must do another query.
	//
	private class SearchLocationSrv extends AsyncTask<Void, Void, List<Place>>{
		private String searchName;
		private Location l;
		public SearchLocationSrv(Location l, String searchname) {
			this.searchName = searchname;
			this.l = l;
		}
		
		
		//This execute method return null if the query search fail.
    	@Override
    	protected List<Place> doInBackground(Void... params) {
    		List<Place> places = null;
    		try {
    			//search by Location and searchName
    			places = new PlaceRequest().searchByLocation(l, searchName); 
    		} catch (Exception e) {
    			Log.v(PlaceRequest.LOG_KEY, "SearchLocationSrvRequest fail");
    			e.printStackTrace();
    		}
    		return places;
    	}
    	
    	@Override
    	protected void onPostExecute(List<Place> result) {
    		//UI Thread to update the GUI.
    		//Display Places selction GUI
    		String text = "Result \n";
			if (result!=null){
				for(Place place: result){//loop through the place
					
					//I was drawing the places with pin on the map earlier on. 
					//this is how it is done, just want to leave it here in case there is use later.
//					Drawable customPin = createCustomPin(place.getBestType());
//					int placeLocationX = (int) (place.getLat() * 1E6);
//					int placeLocationY = (int) (place.getLng() * 1E6);
//			        GeoPoint placeLocation = new GeoPoint(placeLocationX, placeLocationY);
//			        OverlayItem overlayItem = new OverlayItem(placeLocation, place.getName(), place.getVicinity());
//			        CustomPinPoint custom = new CustomPinPoint(customPin, WeShouldActivity.this);
//			        custom.insertPinpoint(overlayItem);
//			        overlayList.add(custom);
					text = text + place.getName() +"\n";
				}
			}
//			Testing SearchPlaceDetailSrv
//			SearchPlaceDetailSrv request = new SearchPlaceDetailSrv(result.get(0));
//			request.execute();
			
			setProgressBarIndeterminateVisibility(false);
    	}
    }
	
	
	//
	//SearchPlaceDetailSrv is an AsynTask that query on a certain place for more detail
	//It attempt to get a Detail Place includes the address, phone number, website, etc.
	//Look at DetailPlace to see what extra data does it support.
	//
	//In its constructor, it take a reference String.  referenceString can be get
	//by Place object, return values of SearchLocationSrv query.
	//
	private class SearchPlaceDetailSrv extends AsyncTask<Void, Void, DetailPlace>{
		private String reference;
		
		
		public SearchPlaceDetailSrv(String reference) {
			this.reference = reference;
		}
		
		//This method return null when there is any exception.
		//return a DetailPlace Object otherwise.
    	@Override
    	protected DetailPlace doInBackground(Void... params) {
    		DetailPlace detailplace = null;
    		try {
    			//doing the query
    			detailplace = new PlaceRequest().searchPlaceDetail(reference);
    		} catch (Exception e) {
    			Log.v(PlaceRequest.LOG_KEY, "SearchLocationSrvRequest fail");
    			e.printStackTrace();
    		}
    	
    		return detailplace;
    	}
    	
    	@Override
    	protected void onPostExecute(DetailPlace result) {	
    		if(result != null) {
    			//UI thread, update user's view and store to database.
    			String text = "Result \n";
    			if (result!=null){
    			}
    			setProgressBarIndeterminateVisibility(false);
    		}
    	}
    }
	
	
	//Example of how to create drawable to pass into CustomPinPoint to draw on the map
	private Drawable createCustomPin(PlaceType type) {
	   switch(type) {
//	   		case UNIVERSITY:
//	   			return getResources().getDrawable(R.drawable.university);
//	   		case RESTAURANT:
//	   			return getResources().getDrawable(R.drawable.restaurant);
//	   		case MOVIE_RENTAL:
//	   			return getResources().getDrawable(R.drawable.movie_rental);
//	   		case MOVIE_THEATER:
//	   			return getResources().getDrawable(R.drawable.movie_theater);
//	   		case CAFE:
//	   			return getResources().getDrawable(R.drawable.coffee);
//	   		case BAR:
//	   			return getResources().getDrawable(R.drawable.bar);
	   		default:
	   			return null;
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