package we.should;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
public class WeShouldActivity extends MapActivity implements LocationListener{
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	private MapView map;
	private LocationManager lm;
	private MapController controller;
	private String towers;
	private int devX, devY;
	private MyLocationOverlay myLocationOverlay;
    
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
		}
		return true;
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
			      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
			          Toast.LENGTH_SHORT).show();
			    }
			  });
			
			return lv;
		}
		
	}
    
}