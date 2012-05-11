package we.should;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Item;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class WeShouldActivity extends MapActivity implements LocationListener {
	
	/** The key for the item in edit and view screens. **/
	public static final String ITEM = "ITEM";
	
	/** The key for creating categories. **/
	private static final int NEW_CAT = 0;
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	
	/** A map that maps the name of each category to its in-memory representation. **/
	private Map<String, Category> mCategories;
	
	private MapView map;
	private LocationManager lm;
	private MapController controller;
	private String towers;
	private int devX, devY;
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
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup();
        updateTabs();        

        db = new WSdb(this);
        
        //Testing
        myLocationOverlay = new MyLocationOverlay(this, map);
        map.getOverlays().add(myLocationOverlay);
        //Getting current location.
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // it is a string
        Criteria crit = new Criteria();
        towers = (lm.getBestProvider(crit, false)); //getting best provider.
        Location location = lm.getLastKnownLocation(towers);
        if(location != null) {
	        devX = (int) (location.getLatitude() * 1E6);
	        devY = (int) (location.getLongitude() * 1E6);
	        GeoPoint ourLocation = new GeoPoint(devX, devY);
	        controller.animateTo(ourLocation);
	        controller.setZoom(18);
	        
        } else {
        	Toast.makeText(WeShouldActivity.this, 
        			"Couldn''t get provider", Toast.LENGTH_SHORT).show();
        }
        map.postInvalidate();

        
    }

    /**
     * Updates the tabs on startup or when categories change.
     */
	private void updateTabs() {
		mCategories = new HashMap<String, Category>();
		for (Category cat : Category.getCategories(this)) {
			mCategories.put(cat.getName(), cat);
		}
		
		TabHost.TabSpec spec;
        if (mCategories.size() > 0) {
            TabPopulator tp = new TabPopulator();
	        for (String name : mCategories.keySet()) {
		        spec = mTabHost.newTabSpec(name).setIndicator("  " + name + "  ")
		        		.setContent(tp);
		        mTabHost.addTab(spec);
		        mTabHost.setCurrentTab(0);
	        }
        } else {
        	// Do something!?
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
		case R.id.add_cat:
			intent = new Intent(this, NewCategory.class);
			startActivityForResult(intent, NEW_CAT);
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateTabs();
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
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
			lv.setAdapter(new ArrayAdapter<Item>(getApplicationContext(),
					      R.layout.item_row, itemsList));
			
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	Item item = itemsList.get(position);
					Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
					intent.putExtra(ITEM, item);
					startActivity(intent);
			    }
			  });
			
			return lv;
		}
		
	}
	
}