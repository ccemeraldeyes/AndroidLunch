package we.should;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;


public class WeShouldActivity extends MapActivity implements LocationListener{
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	private MapView map;
	private LocationManager lm;
	private int devX, devY;
	private MapController controller;
	private String towers;
	
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
        View view;
        
        spec = mTabHost.newTabSpec("restaurants").setIndicator("  Restaurants  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec("movies").setIndicator("  Movies  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);
        
        spec = mTabHost.newTabSpec("other").setIndicator("  Other  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);

        for (int i = 0; i < 10; i++) {
	        spec = mTabHost.newTabSpec("other").setIndicator("  Other " + i + "  ")
	        		.setContent(android.R.id.list);
	        mTabHost.addTab(spec);
        }

        mTabHost.setCurrentTab(0);
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // it is a string
        Criteria crit = new Criteria();
        towers = (lm.getBestProvider(crit, false)); //getting best provider.
        Location location = lm.getLastKnownLocation(towers);
        if(location != null) {
	        devX = (int) (location.getLatitude() * 1E6);
	        devY = (int) (location.getLongitude() * 1E6);
	        GeoPoint ourLocation = new GeoPoint(devX, devY);
	        controller.animateTo(ourLocation);
	        controller.setZoom(6);
	        
        } else {
        	Toast.makeText(WeShouldActivity.this, 
        			"Couldn''t get provider", Toast.LENGTH_SHORT).show();
        }
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}