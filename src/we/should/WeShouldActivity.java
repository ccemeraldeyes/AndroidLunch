package we.should;

import java.util.ArrayList;
import we.should.database.*;
import java.util.Arrays;
import java.util.List;

import we.should.search.CustomPinPoint;
import we.should.search.Place;
import we.should.search.PlaceRequest;
import we.should.search.PlaceType;
import we.should.database.WSdb;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class WeShouldActivity extends MapActivity implements LocationListener{
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	private MapView map;
	private LocationManager lm;
	private MapController controller;
	private String towers;
	private int devX, devY;
	private List<Overlay> overlayList;
	private MyLocationOverlay myLocationOverlay;
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
        

        db = new WSdb(this);
        //DBFILE = db.getDB().getPath();
        
        // Just spoof the tabs for the ZFR.  This would be dynamically loaded
        // once we begin work on production code.
        // pittsw: 4/20/12
        TabHost.TabSpec spec;
        TabPopulator tp = new TabPopulator();
        spec = mTabHost.newTabSpec("restaurants").setIndicator("  Restaurants  ")
        		.setContent(tp);
        mTabHost.addTab(spec);
        spec = mTabHost.newTabSpec("movies").setIndicator("  Movies  ")
        		.setContent(tp);
        mTabHost.addTab(spec);
        spec = mTabHost.newTabSpec("other").setIndicator("  Other  ")
        		.setContent(tp);
        mTabHost.addTab(spec);
        
        for (int i = 0; i < 10; i++) {
	        spec = mTabHost.newTabSpec("other").setIndicator("  Other " + i + "  ")
	        		.setContent(tp);
	        mTabHost.addTab(spec);
        }

        mTabHost.setCurrentTab(0);
        
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
        setUpBtnSearch();
        map.postInvalidate();
        
    }
    
    
    
    private void setUpBtnSearch() {
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Location l = getDeviceLocation();
				if(l != null) {
//					try {
//						List<Place> places = new PlaceRequest().searchByLocation(l, 5);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					SearchSrv srv = new SearchSrv();
					setProgressBarIndeterminateVisibility(true);
					srv.execute(l);
				}
			}
        	
        });
    }
    
    private class SearchSrv extends AsyncTask<Location, Void, List<Place>>{
    	@Override
    	protected List<Place> doInBackground(Location... location) {
    		List<Place> places = null;
    		Location l;
    		try {
    			l = location[0];
    			places = new PlaceRequest().searchByLocation(l, "university");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return places;
    	}
    	
    	@Override
    	protected void onPostExecute(List<Place> result) {
    		//Display Places selction GUI
    		String text = "Result \n";
			if (result!=null){
				for(Place place: result){
					Drawable customPin = createCustomPin(place.getBestType());
					int placeLocationX = (int) (place.getLat() * 1E6);
					int placeLocationY = (int) (place.getLng() * 1E6);
			        GeoPoint placeLocation = new GeoPoint(placeLocationX, placeLocationY);
			        OverlayItem overlayItem = new OverlayItem(placeLocation, place.getName(), place.getAddress());
			        CustomPinPoint custom = new CustomPinPoint(customPin, WeShouldActivity.this);
			        custom.insertPinpoint(overlayItem);
			        overlayList.add(custom);
					text = text + place.getName() +"\n";
				}
			}
			setProgressBarIndeterminateVisibility(false);
    	}
    }
    
   private Drawable createCustomPin(PlaceType type) {
	   switch(type) {
	   		case UNIVERSITY:
	   			return getResources().getDrawable(R.drawable.university);
	   		case RESTAURANT:
	   			return getResources().getDrawable(R.drawable.restaurant);
	   		case MOVIE_RENTAL:
	   			return getResources().getDrawable(R.drawable.movie_rental);
	   		case MOVIE_THEATER:
	   			return getResources().getDrawable(R.drawable.movie_theater);
	   		case CAFE:
	   			return getResources().getDrawable(R.drawable.coffee);
	   		case BAR:
	   			return getResources().getDrawable(R.drawable.bar);
	   		default:
	   			return null;
	   }
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
    
    
    /*
     * @return the location of the device
     */
    private Location getDeviceLocation() {
    	 lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // it is a string
         Criteria crit = new Criteria();
         towers = (lm.getBestProvider(crit, false)); //getting best provider.
         return lm.getLastKnownLocation(towers);	
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			startActivity(intent);
			break;
		case R.id.add_item:
			intent = new Intent(this, EditScreen.class);
			startActivity(intent);
		}
		return true;
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
		
		private final String[] DATA = {
		    "Afghanistan", "Albania", "Algeria"
		  };

		public View createTabContent(String tag) {
			ListView lv = new ListView(getApplicationContext());
			
			List<String> list = new ArrayList<String>(Arrays.asList(DATA));
			list.add(0, tag);
			lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
					      android.R.layout.simple_list_item_1, list));
			
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			      // When clicked, show a toast with the TextView text
			      Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
			      startActivity(intent);
			    }
			  });
			
			return lv;
		}
		
	}
	
}